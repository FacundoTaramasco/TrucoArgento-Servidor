package come.trucoargento.test;

import com.trucoargento.excepciones.ExcepcionEnvido;
import com.trucoargento.modelo.Envido;
import com.trucoargento.modelo.EnumEnvido;

/**
 *
 * @author FacundoTaramasco
 */
public class Pruebas {
    
    public static void main(String[] args) {
        
        Envido e = new Envido();
        
        try {
        e.agregarEnvido(EnumEnvido.ENVIDO);
        e.agregarEnvido(EnumEnvido.ENVIDO_ENVIDO);
        e.agregarEnvido(EnumEnvido.REAL_ENVIDO);
        e.agregarEnvido(EnumEnvido.FALTA_ENVIDO);
        e.agregarEnvido(EnumEnvido.FALTA_ENVIDO);
        //System.out.println(e.calcularPuntajeAceptado() );
        System.out.println(e.calcularPuntajeRechazado());
        } catch(ExcepcionEnvido ee) {
            System.out.println(ee.getMessage());
        }

    }
}
