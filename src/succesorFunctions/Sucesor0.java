package succesorFunctions;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import main.Estado;
import IA.Azamon.*;

public class Sucesor0 implements SuccessorFunction {
    private boolean meterIntercambiar;
    private Paquetes P;
    private Transporte T;

    public Sucesor0(boolean metInt) {
        meterIntercambiar = metInt;
    }

    public List getSuccessors(Object state) {
        int count = 0;
        ArrayList<Successor> ret = new ArrayList<Successor>();
        Estado actual = (Estado) state;
        P = actual.getPaquetes();
        T = actual.getTransporte();

        ArrayList<Double> actualEspLibre = actual.getEspLibre();
        ArrayList<Integer> actualAsig = actual.getAsig();

        int actualFelicidad = actual.getFelicidad();
        double actualCostes = actual.getCostes();

        for(int i=0; i<P.size(); ++i) {
            int ofetaActual = actualAsig.get(i);
            for(int j=0; j<T.size(); ++j) {
                if(j!=ofetaActual && actualEspLibre.get(j) >= P.get(i).getPeso() && cumplePrio(P.get(i).getPrioridad(), T.get(j).getDias())) {
                    ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>)actualEspLibre.clone());
                    ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>)actualAsig.clone());

                    int newFelicidad = actualFelicidad;
                    double newCostes = actualCostes;
                    Estado sucesor = new Estado(newFelicidad,newCostes, newEspLibre,newAsig);

                    set(sucesor, i, j); // aplicamos el operador set
                    // añadimos el sucesor a la lista
                    //System.out.println("Metemos el paquete " + i + " en la oferta" + j);
                    ret.add(new Successor("Metemos el paquete " + i + " en la oferta" + j, sucesor));

                }
            }
        }
        //System.out.println("pasamos de iteracion");
        if (meterIntercambiar){
            for (int i=0; i<P.size(); ++i){
                for (int j=i+1; j<P.size(); ++j){
                    //Condiciones de aplicabilidad: que ambos quepan y que se cumplan prioridades
                    int ofertaI = actualAsig.get(i);
                    int ofertaJ = actualAsig.get(j);
                    boolean cabeI = (actualEspLibre.get(ofertaJ)+P.get(j).getPeso()>=P.get(i).getPeso());
                    boolean cabeJ = (actualEspLibre.get(ofertaI)+P.get(i).getPeso()>=P.get(j).getPeso());
                    boolean cumplePrioI = cumplePrio(P.get(i).getPrioridad(),T.get(ofertaJ).getDias());
                    boolean cumplePrioJ = cumplePrio(P.get(j).getPrioridad(),T.get(ofertaI).getDias());
                    if (cabeI && cabeJ && cumplePrioI && cumplePrioJ && ofertaI!=ofertaJ){
                        int newFelicidad = actualFelicidad;
                        double newCostes = actualCostes;
                        ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>)actualEspLibre.clone());
                        ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>)actualAsig.clone());
                        //Creamos el nuevo estado con estos datos
                        Estado sucesor = new Estado(newFelicidad, newCostes, newEspLibre, newAsig);
                        //Cambiamos los datos del sucesor
                        intecambiamosIconJ(sucesor, i, j);
                        Successor anadir = new Successor("intercambiamos " + i + " con " + j, sucesor);
                        ret.add(anadir);
                        //System.out.println("intercambiamos" + i + " con " + j);
                    }
                }
            }
            //System.out.println("pasamos de iteracion");
        }
        return ret;
    }

    private void intecambiamosIconJ(Estado e, int i, int j) {
        Paquetes p = e.getPaquetes();
        Transporte t = e.getTransporte();

        //Deshacemos espacio libre
        ArrayList<Double> espLibre = (ArrayList<Double>)e.getEspLibre().clone();
        int ofertaAnteriorI = e.getAsig().get(i);
        int ofertaAnteriorJ = e.getAsig().get(j);

        espLibre.set(ofertaAnteriorI, espLibre.get(ofertaAnteriorI)+p.get(i).getPeso()-p.get(j).getPeso());
        espLibre.set(ofertaAnteriorJ, espLibre.get(ofertaAnteriorJ)+p.get(j).getPeso()-p.get(i).getPeso());

        //Hacemos la nueva asignación
        e.setAsig(i,ofertaAnteriorJ);
        e.setAsig(j,ofertaAnteriorI);

        //Seteamos felicidad costes y espacio libre
        e.calculaCoste();
        e.calculaFelicidad();
        e.setEspLibre(espLibre);
    }

    // pre: el Estado e es un estado inicializado. El Paquete identificado por i cumple todas las condiciones para ser
    //      asignado a la oferta j
    // post: el Paquete identificado por i pasa a ser asignado a la oferta j, actualizando los valores del Estado e para ser
    //       coherentes con su representación
    private void set(Estado e, int i, int j) {
        Paquetes p = e.getPaquetes();
        Transporte t = e.getTransporte();

        //Deshacemos espacio libre
        ArrayList<Double> espLibre = (ArrayList<Double>)e.getEspLibre().clone();
        int ofertaAnterior = e.getAsig().get(i);
        espLibre.set(ofertaAnterior, espLibre.get(ofertaAnterior)+p.get(i).getPeso());

        //Hacemos la nueva asignación
        e.setAsig(i,j);

        espLibre.set(j,espLibre.get(j)-p.get(i).getPeso());

        //Seteamos felicidad costes y espacio libre
        e.calculaCoste();
        e.calculaFelicidad();
        e.setEspLibre(espLibre);
    }

    // pre: cierto
    // post: retorna cierto sii la prioridad prio es compatibe con el número de días nDias
    private boolean cumplePrio(int prio, int nDias) {
        if(prio==0 && nDias==1) return true;
        if(prio==1 && nDias<=3) return true;
        if(prio==2) return true;
        return false;
    }
}