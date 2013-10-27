package jts.moteur.ligne.voie;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jts.io.SauvegardableBinaire;
import jts.io.SauvegardableXml;
import jts.io.xml.AttributXml;
import jts.io.xml.ElementXml;
import jts.moteur.geometrie.AngleEuler;
import jts.moteur.geometrie.Point;
import jts.moteur.geometrie.Polygone;
import jts.moteur.ligne.signalisation.PanneauLumineux;
import jts.moteur.ligne.voie.elements.CourbeElementaire;
import jts.moteur.ligne.voie.elements.Segment;
import jts.moteur.ligne.voie.points.PointExtremite;

import org.w3c.dom.Element;

/**Cette classe repr�sente une section de voie ferr�e.
 * 
 * @author Yannick BISIAUX
 *
 */
public class Section implements SauvegardableBinaire, SauvegardableXml {

	private boolean absolu;
	private Point positionAbsolue;
	private AngleEuler angle;
	private List<PointExtremite> pointsPassages;
	private List<CourbeElementaire> elements;
	private Polygone frontiere;
	
	private String nomObjet;
	
	public Section(){
		this(new Point(), new AngleEuler());
	}
	
	public Section(Point position, AngleEuler angle){
		this.positionAbsolue = position;
		this.angle = angle;
		this.pointsPassages = new ArrayList<PointExtremite>();
		this.elements = new ArrayList<CourbeElementaire>();
		absolu = false;
	}
	
	public Point getPositionAbsolue() { return positionAbsolue;	}
	
	public AngleEuler getAngle(){ return this.angle; }
	
	public void addPoint(PointExtremite point){ this.pointsPassages.add(point);	}

	public List<PointExtremite> getPointsPassages() { return pointsPassages;	}
	
	public void addElement(CourbeElementaire element){ this.elements.add(element);	}

	public List<CourbeElementaire> getElements() { return elements;	}
	
	public void creerFrontiere(List<Point> sommets){
		this.frontiere = Polygone.createPolygone(sommets);
	}

	public Polygone getFrontiere() { return frontiere; }
	
	public String getNomObjet(){ return this.nomObjet; }
	
	public void setNomObjet(String nomObjet){ this.nomObjet = nomObjet; }
	
	/**Translate les �l�ments dans leur position absolue si n�cessaire.
	 * 
	 */
	public void rendreAbsolu(){
		if (!absolu){
			for (PointExtremite pp : pointsPassages){
				pp.transformer(positionAbsolue, angle.getPsi(), angle.getTheta());
			}
			for (CourbeElementaire element : elements){
				element.transformer(positionAbsolue, angle.getPsi(), angle.getTheta());
			}
			if(frontiere != null){
				for (Point point : frontiere.getSommets()){
					point.transformer(positionAbsolue, angle.getPsi(), angle.getTheta());
				}
			}
		}
		absolu = true;
	}
	
	public boolean isInside(Point p){
		return false;
	}
	
	public void addPanneauLumineux(PanneauLumineux panneauLumineux, int indiceElement, double distance, boolean sensDirect){
		Point p = new Point();
		CourbeElementaire element = elements.get(indiceElement);
		element.recupererPosition(p, new AngleEuler(), distance, sensDirect);
		panneauLumineux.transformer(p, 0, 0);
		this.pointsPassages.add(panneauLumineux);
		
		PointExtremite p1 = element.getP1();
		PointExtremite p2 = element.getP2();
		
		CourbeElementaire element1;
		CourbeElementaire element2;
		if (sensDirect){
			element1 = new Segment(p1, panneauLumineux, element.getTheta());
			element2 = new Segment(panneauLumineux, p2, element.getTheta());
			p1.setElement(element1);
			p2.setElement(element2);
			panneauLumineux.setElement(element1);
			panneauLumineux.setElement(element2);
		} else {
			element1 = new Segment(p2, panneauLumineux, element.getTheta());
			element2 = new Segment(panneauLumineux, p1, element.getTheta());
			p2.setElement(element1);
			p1.setElement(element2);
			panneauLumineux.setElement(element2);
			panneauLumineux.setElement(element1);
		}
		
		this.elements.remove(element);
	}

	public void load(DataInputStream dis) throws IOException {
		//Reconstitution de la position et de l'orientation
		positionAbsolue.load(dis);
		angle.load(dis);
		
		//Reconstitution des points d'entr�e
		int nEntree = dis.readShort();
		for(int i=0; i<nEntree; i++){
			/*PointPassage pp = new PointPassage();
			pp.load(dis);
			this.addPoint(pp);*/
		}
		
		//Reconstitution de la frontiere
		//TODO passer le code dans Polygone
		int nFrontiere = dis.readShort();
		List<Point> sommets = new ArrayList<Point>();
		for(int i=0; i<nFrontiere; i++){
			Point point = new Point();
			point.load(dis);
			sommets.add(point);
		}
		frontiere = Polygone.createPolygone(sommets);
	}

	public void save(DataOutputStream dos) throws IOException {
		//Sauvegarde de la position et de l'orientation
		positionAbsolue.save(dos);
		angle.save(dos);
		
		//Sauvegarde des points d'entr�e
		dos.writeShort(pointsPassages.size());
		for (PointExtremite pp : pointsPassages){
			pp.save(dos);
		}
		
		//Sauvegarde de la frontiere
		//TODO passer le code dans Polygone
		dos.writeShort(frontiere.getSommets().size());
		for (Point point : frontiere.getSommets()){
			point.save(dos);
		}
	}

	public void load(Element element) throws IOException {
		
	}

	public void save(String indent, BufferedWriter writer, String nomElement) throws IOException {
		/*writer.write("<?xml version=\"1.0\"?>");
		writer.newLine();
		writer.write("<Section desc=\"StdAig10dD\">");
		writer.newLine();
		writer.write("\t<PointsPassages>");
		writer.newLine();
		for(PointExtremite pointExtremite : pointsPassages){
			pointExtremite.save("\t\t", writer, null);
		}
		writer.write("\t</PointsPassages>");
		writer.newLine();
		writer.write("\t<Frontiere>");
		writer.newLine();
		for(Point point : frontiere.getSommets()){
			point.save("\t\t", writer, null);
		}
		writer.write("\t</Frontiere>");
		writer.newLine();
		writer.write("</Section>");
		writer.newLine();*/
		writer.write("<?xml version=\"1.0\"?>");
		writer.newLine();
		this.save().write(indent, writer);
	}
	
	public ElementXml save(){
		ElementXml element = new ElementXml("Section");
		element.addAttribut(new AttributXml("desc", "StdAig10dD"));
		ElementXml ppElement = new ElementXml("PointsPassages");
		element.addElement(ppElement);
		for(PointExtremite pointExtremite : pointsPassages){
			ppElement.addElement(pointExtremite.save());
		}
		ElementXml frontiereElement = new ElementXml("Frontiere");
		element.addElement(frontiereElement);
		for(Point point : frontiere.getSommets()){
			frontiereElement.addElement(point.save());
		}
		
		return element;
	}
}
