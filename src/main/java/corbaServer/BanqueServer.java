
package corbaServer;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import service.BanqueImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class BanqueServer {

    public static void main(String[] args) {

        try {
            // 1. Initialiser l'ORB
            ORB orb = ORB.init(args, null);

            // 2. Initialiser le RootPOA
            POA rootPOA = POAHelper.narrow(
                    orb.resolve_initial_references("RootPOA")
            );

            rootPOA.the_POAManager().activate();

            // 3. Configurer JNDI directement
            Properties props = new Properties();

            props.put(
                    Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.cosnaming.CNCtxFactory"
            );

            props.put(
                    Context.PROVIDER_URL,
                    "iiop://localhost:900"
            );

            Context ctx = new InitialContext(props);

            // 4. Créer le servant
            BanqueImpl banque = new BanqueImpl();

            // 5. Obtenir la référence CORBA
            org.omg.CORBA.Object ref =
                    rootPOA.servant_to_reference(banque);

            // 6. Enregistrer le service dans l'annuaire
            ctx.rebind("banque", ref);

            System.out.println(
                    "Objet 'banque' enregistré dans l'annuaire."
            );

            // 7. Attendre les requêtes des clients
            System.out.println(
                    "Serveur CORBA Banque démarré, en attente des clients..."
            );

            orb.run();

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
