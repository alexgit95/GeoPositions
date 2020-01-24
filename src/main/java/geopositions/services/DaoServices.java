package geopositions.services;

import java.util.List;

import geopositions.model.GeoPosition;

public interface DaoServices {

	/**
	 * Permet d'inserer une nouvelle position
	 * @param toCreate
	 */
	void insert(GeoPosition toCreate);
	
	
	/**
	 * Permet de recuperer les infos de toutes les positions enregistre
	 * @return
	 */
	List<GeoPosition> getAll();
	
	/**
	 * Permet de recuperer toutes les infos d'une année
	 * @param year
	 * @return
	 */
	List<GeoPosition> getPositionsByYear(int year);


	/**
	 * Permet de recuperer toutes les positions d'un mois
	 * @param year
	 * @param month
	 * @return
	 */
	List<GeoPosition> getPositionsByMonth(int year, int month);


	/**
	 * Permet de recuperer toutes les postions proche de l'endroit passé en argument
	 * @param lattitude
	 * @param longitude
	 * @return
	 */
	List<GeoPosition> getPositionsByPosition(double lattitude, double longitude);
}
