import java.util.HashSet;

public class Porto {

    private static HashSet<Integer> listaPorto = new HashSet<>();
   public static int portoAtual = 1024;
   int porto = (int) (Math.random() * ( 47808 - 1058 ));

    public static int getPortoAtual() {
        return portoAtual;
    }
    public void aumentaPorto(){
        portoAtual++;
    }
/*
    static int escolhePorto() {
        int porto;
        //Porto 1024 inicial
        //Porto 10024 final
        if(listaPorto.size() == 9000){
            return 0;//Nao existe portos disponiveis
        }
        for(porto = 1024 ; porto < 10024; porto++){

            if(!listaPorto.contains(porto)){
                addPorto(porto);
                break;
            }

        }

        return porto;
    }
*/
    int escolhePorto() {
        return this.porto;
    }

    boolean deletePorto(int porto){

        if (listaPorto.contains(porto)) {
            listaPorto.remove(porto);
            return true;
        //porto ja utilizado
        } else {
            return false;//porto nao utilizado
        }

    }

    static boolean addPorto(int porto){

        if (listaPorto.contains(porto)) {
            return false;
            //porto ja utilizado
        } else {
            listaPorto.add(porto);
            return true;//porto nao utilizado
        }

    }



}
