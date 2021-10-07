package com.example.project_wolf_3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.juang.jplot.PlotBarritas;
import com.juang.jplot.PlotPastelito;
import com.juang.jplot.PlotPlanitoXY;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Retrofit;

public class GraficaActivity extends AppCompatActivity {
    private static final String TAG ="BITCOIN";
    private Retrofit retrofit;
    Timer tiempo;
    PlotBarritas Columna;
    PlotBarritas ColumnaAgrupada;
    PlotBarritas ColumnaApilada;
    PlotBarritas ColumnaApilada100;
    public  ArrayList<Object> listardata = new ArrayList<>();
    TextView preciobtc;
    Context context;
    private LinearLayout pantalla;

    Timer timer;
    private PlotPlanitoXY plot;
    private PlotPastelito pastel;



    private Button inicia,butpastel,btnMidleCircle;

    int i=0; // contador de datos

    float [] Xd,Yd;
    private float[] X=new float [4000];//almacenado de
    private float[] Y=new float [4000];//datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica);
        context=this;
        pantalla= (LinearLayout) (findViewById(R.id.grafico));
        preciobtc = findViewById(R.id.preciobtc);
        obtenerDatos();
        //inicia();
        //columna_apil100(pantalla);


    }
    public void inicia(){
        // inicializamos el grafico dinamico ó serie de tiempo
        plot = new PlotPlanitoXY(context,"xx vs yy","x","y");//el "context" si no esta dentro del hilo UI puede simplemente colocarse this
        plot.SetEscalaAutomatica(true);// escala automatica si no se coloca true deben definirse los valores maximos de xmin,xmax,y1min,y1max,y2min,y2max
        plot.SetHD(true);

        String url = "http://10.0.2.2:3050/precios";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonObject = new JSONArray(response);
                    HashMap<String, Double> listar = new HashMap<>();
                    ArrayList<Object> listdata = new ArrayList<>();
                     ArrayList<Double> listvalor = new ArrayList<>();
                    for (int i=0; i<jsonObject.length(); i++){
                        String Nombre = jsonObject.getJSONObject(i).getString("nombre");
                        Double Valor = jsonObject.getJSONObject(i).getDouble("precio");
                        listar.put(Nombre,Valor);
                        listdata.add(listar);
                        listvalor.add(Valor);
                        preciobtc.setText(String.valueOf(listvalor.get(i)));
                        //Toast.makeText(getApplicationContext(), "" + listdata.get(j), Toast.LENGTH_SHORT).show();
                    }
                    listardata.add(listvalor);

                    //Log.e(TAG, "Nombre: "+ listdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: "+ error.getMessage() );
            }
        });

        Volley.newRequestQueue(this).add(postRequest);

        timer = new Timer();
        TimerTask task = new TimerTask()
        {
            /* se inicia un hilo en paralelo para ejecutar la tarea asincrona podria usarse tambien la clase AnsycTask y usar su
               su hilo especifico para acceder a la UI */
            @Override
            public void run()
            {
                //hilo para comunicarse con la UI intefaz de usuario y poder pintar el el LinearLayout "pantalla"
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pantalla.removeAllViews();
                        Xd=new float[i+1]; Xd[0]=3.4f;
                        Yd=new float[i+1];
                        X[i]=i-20;
                        Y[i]=(i-20)*(i-20);
                        for (int j = 0; j < Xd.length; j++) {
                            Xd[j]= X[j];
                            Yd[j] =Y[j];
                        }
                        for (int k=0; k<listardata.size(); k++) {
                            preciobtc.setText(String.valueOf( listardata.get(k)));
                            //Toast.makeText(getApplicationContext(), "" + listardata.get(k), Toast.LENGTH_SHORT).show();
                        }
                        plot.SetSerie1(Xd,Yd,"graph1",5,true);// el true indica que unira los puntos con recta
                        pantalla.addView(plot);
                        i=i+1;
                    }//hilo2
                });//hilo para acceder a la intefaz grafica
            }//hilo1
        };
        timer.schedule(task, 1000, 200);// el timer se ejecura despues de 1000ms=1seg de haber precionando el boton "plot2d" y se repite cada 200ms
    }

    public void columna(View v){

        String x[]={"lunes","martes","miercoles","jueves","viernes","sabado","domingo"};
        double y[]={20,30,44,0,-25};
        Columna=new PlotBarritas(context,"Gráfico de Columnas ","articulos vendidos por dia");
        //en context puede colocarse simplemente this
        //personalizacion del grafico
        Columna.Columna(x,y);
        Columna.SetHD(true);
        //cambiemos el color del dato 3 o sea "44" rojo=255,verde=0,Azul=0 los ultimos res enteros son los colores en rgb
        Columna.SetColorPila(3,255,0,0);//muestra el tercer dato en color rojo

        //mostrando en pantalla
        pantalla.removeAllViews();
        pantalla.addView(Columna);
    }
    public void columna_agru(View v){
        String x[]={"lunes","martes","miercoles","jueves","viernes","sabado","domingo"};
        String Acota[]={"jeans","shorts","shoes"};
        double y[][]={{2 ,3,1},//y[]{]  defina un array de 7 grupos con 3 columnas  puede ser de y[n][m] con n,m cualquier entero
                {5 ,2,5},
                {1,3,2},
                {0 ,3,1},
                {2 ,4,-1},
                {2 ,0,-1},
                {2 ,1,-1}};

        ColumnaAgrupada=new PlotBarritas(this,"Gráfico de Columnas Agrupadas","articulos vendidos por dia");

        //personalizacion de grafico
        ColumnaAgrupada.ColumnaAgrupada(x,y,Acota);
        ColumnaAgrupada.SetSizeAcot(15);
        ColumnaAgrupada.SetSizeTitulo(20);
        ColumnaAgrupada.SetSizeTituloY(12);
        ColumnaAgrupada.SetHD(true);
        ColumnaAgrupada.SetColorContorno(255,0,0);//contorno en rojo
        ColumnaAgrupada.SetColorPila(2,255,0,0);//segunda columna de grupo en rojo

        pantalla.removeAllViews();
        pantalla.addView(ColumnaAgrupada);
    }

    public void columna_apil(View v){
        String x[]={"lunes","martes","miercoles","jueves","viernes","sabado","domingo"};
        String Acota[]={"jeans","shorts","shoes"};
        double y[][]={{2 ,3,1},//y[7]{3]  defina un array de 7 grupos con 3 columnas  puede ser de y[n][m] con n,m cualquier entero
                {5 ,2,5},
                {1,3,2},
                {0 ,3,1},
                {2 ,4,-1},
                {2 ,0,-1},
                {2 ,1,-1}};

        ColumnaApilada=new PlotBarritas(this,"Gráfico de Columnas Agrupadas","articulos vendidos por dia");

        //personalizacion de grafico
        ColumnaApilada.ColumnaApilada(x,y,Acota);
        ColumnaApilada.SetHD(true);
        ColumnaApilada.SetColorContorno(255,0,2355);//contorno en magenta
        ColumnaApilada.SetColorPila(1,0,0,255);//primer pila de columna en azul  recordar que los ultimos 3 enteros son rgb
        ColumnaApilada.SetColorPila(2,0,255,255);//segunda pila de columna en cyan  recordar que los ultimos 3 enteros son rgb
        //puede elegirse el color hasta la pila 44 si es mayor sera generado aleatoriamente

        pantalla.removeAllViews();
        pantalla.addView(ColumnaApilada);
    }



    public void columna_apil100(View v){
        String x[]={"lunes","martes","miercoles","jueves","viernes","sabado","domingo"};
        String Acota[]={"jeans","shorts","shoes"};
        double y[][]={{2 ,3,1},//y[7]{3]  defina un array de 7 grupos con 3 columnas  puede ser de y[n][m] con n,m cualquier entero
                {5 ,2,5},
                {1,3,2},
                {0 ,3,1},
                {2 ,4,-1},
                {2 ,0,-1},
                {2 ,1,-1}};
        ColumnaApilada100=new PlotBarritas(this,"Gráfico de Columnas Agrupadas","articulos vendidos por dia");

        //personalizacion de grafico
        ColumnaApilada100.ColumnaApilada100(x,y,Acota);
        ColumnaApilada100.SetHD(true);
        ColumnaApilada100.SetContorno(0);//sin contorno mayor a cero aparece el contorno por default es blanco a este metodo solo debe pasarsele un valor entre 0 y 10 mayor a eso no se toma en cuenta
        ColumnaApilada100.SetColorPila(1,255,105,180);//primera pila de columna de color hot pink
        ColumnaApilada100.SetColorPila(2,255,255,0);//segunda pila de columna de color amarillo
        pantalla.removeAllViews();
        pantalla.addView(ColumnaApilada100);
    }

    private void obtenerDatos(){
        String url = "http://10.0.2.2:3050/precios";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonObject = new JSONArray(response);
                    HashMap<String, Double> listar = new HashMap<>();
                    ArrayList<Object> listdata = new ArrayList<>();
                    ArrayList<Double> listvalor = new ArrayList<>();
                    for (int i=0; i<jsonObject.length(); i++){
                        String Nombre = jsonObject.getJSONObject(i).getString("nombre");
                        Double Valor = jsonObject.getJSONObject(i).getDouble("precio");
                        listar.put(Nombre,Valor);
                        listdata.add(listar);
                        listvalor.add(Valor);
                        preciobtc.setText(String.valueOf(listvalor.get(i)));
                            //Toast.makeText(getApplicationContext(), "" + listdata.get(j), Toast.LENGTH_SHORT).show();
                    }
                    listardata.add(listdata);

                    //Log.e(TAG, "Nombre: "+ listdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: "+ error.getMessage() );
            }
        });
        Volley.newRequestQueue(this).add(postRequest);
    }
/*    private void obtenerDatos() {
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        Call<PrecioRespuesta> precioRespuestaCall = service.obtenerListaPrecio();

        precioRespuestaCall.enqueue(new Callback<PrecioRespuesta>() {
            @Override
            public void onResponse(Call<PrecioRespuesta> call, Response<PrecioRespuesta> response) {
                if (response.isSuccessful()){
                    PrecioRespuesta precioRespuesta = response.body();
                    ArrayList<Precio> listaPrecio = precioRespuesta.getResults();
                    for (int i = 0; i<listaPrecio.size(); i++){
                        Precio p = listaPrecio.get(i);
                        Log.i(TAG, "Precio: "+ p.getNombre());
                    }


                }else{
                    Log.e(TAG, " onResponse: " + response.errorBody());

                }
            }

            @Override
            public void onFailure(Call<PrecioRespuesta> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
*/
}