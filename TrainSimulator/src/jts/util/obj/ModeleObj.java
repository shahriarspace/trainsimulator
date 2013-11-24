package jts.util.obj;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jts.moteur.geometrie.Point;
import jts.util.obj.Groupe;
import jts.util.obj.Surface;

public class ModeleObj {
	
	private List<Point> points;
	private List<Point> pointsTexture;
	private List<Groupe> groupes;

	public ModeleObj(){
		this.points = new ArrayList<Point>();
		this.pointsTexture = new ArrayList<Point>();
		this.groupes = new ArrayList<Groupe>();
	}
	
	public void addPoint(Point point){ this.points.add(point); }
	
	public List<Point> getPoints(){ return this.points; }
	
	public void addPointTexture(Point point){ this.pointsTexture.add(point); }
	
	public List<Point> getPointsTexture(){ return this.pointsTexture; }
	
	public void addGroupe(Groupe groupe){ this.groupes.add(groupe); }
	
	public List<Groupe> getGroupes(){ return this.groupes; }
	
	public void transformer(Point translation){
		for(Point p : points){
			p.transformer(translation);
		}
	}
	
	public void write(File fichier){
		try {
			FileWriter fw = new FileWriter(fichier);
			BufferedWriter buffer = new BufferedWriter(fw);
			
			buffer.write("mtllib textures_sections.mtl");
			buffer.newLine();
			
			for(Point point : points){
				buffer.write("v " + point.getY() + " " + point.getZ() + " " + point.getX());
				buffer.newLine();
			}
			
			for(Point point : pointsTexture){
				buffer.write("vt " + point.getX() + " " + point.getY());
				buffer.newLine();
			}
			
			for(Groupe groupe : groupes){
				buffer.write("g " + groupe.getName());
				buffer.newLine();
				buffer.write("usemtl " + groupe.getMtlName());
				buffer.newLine();
				buffer.write("s off");
				buffer.newLine();
				for(Surface surface : groupe.getSurfaces()){
					buffer.write("f");
					for(int i=0; i<surface.getPoints().size(); i++){
						Point point = surface.getPoints().get(i);
						buffer.write(" " + (points.indexOf(point)+1));
						if(i<surface.getPointsTexture().size()){
							Point pt = surface.getPointsTexture().get(i);
							buffer.write("/" + (pointsTexture.indexOf(pt)+1));
						}
					}
					buffer.newLine();
				}
			}
			
			buffer.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
