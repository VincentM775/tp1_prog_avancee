package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Point d'entrée JAX-RS.
 *
 * Choix de configuration : on utilise Jersey (implémentation de référence JAX-RS)
 * avec l'annotation @ApplicationPath pour déclarer le préfixe /api.
 * Cette approche est préférée à la configuration via web.xml car elle est :
 * - Plus lisible et centralisée
 * - Conforme aux standards Jakarta EE
 * - Sans dépendance à un fichier XML externe
 *
 * Jersey est choisi plutôt que RESTEasy car c'est l'implémentation de référence
 * de la spécification JAX-RS, il est bien documenté et largement utilisé.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
