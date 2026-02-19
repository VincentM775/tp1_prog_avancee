package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
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
@OpenAPIDefinition(
    info = @Info(
        title = "MasterAnnonce API",
        version = "1.0",
        description = "API REST de gestion d'annonces - TP Dev Avancé #3 (BUT 3 IUT Montreuil). "
                + "Permet de créer, modifier, publier, archiver et supprimer des annonces. "
                + "Authentification stateless par token Bearer.",
        contact = @Contact(name = "Groupe 22", url = "https://github.com/univ-paris8")
    ),
    servers = {
        @Server(url = "/demo1", description = "Serveur local (Tomcat)")
    }
)
public class RestApplication extends Application {
}
