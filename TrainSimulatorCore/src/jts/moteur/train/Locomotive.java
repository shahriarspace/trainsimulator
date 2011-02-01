package jts.moteur.train;

/**Cette classe représente une locomotive.
 * 
 * @author Yannick BISIAUX
 *
 */
public class Locomotive extends Wagon {
	
	public Locomotive(float longueur, float largeur, float hauteur, float empattement, float masse){
		super(longueur, largeur, hauteur, empattement, masse);
	}

	/**Renvoie la force de traction en kN
	 * 
	 * @param vitesse
	 * @param commandeVitesse
	 * @return
	 */
	public float getForceTraction(float vitesse, float commandeVitesse){
		return commandeVitesse*200;
	}
}
