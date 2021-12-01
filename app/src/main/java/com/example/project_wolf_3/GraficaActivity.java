package com.example.project_wolf_3;

import android.content.Context;
import android.graphics.Color;
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
        //obtenerDatos();
        columna_apil(pantalla);


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
        String Acota[]={"Inversión inicial","Inversion Actual"};
        double y[][]={{200 ,310},//y[7]{3]  defina un array de 7 grupos con 3 columnas  puede ser de y[n][m] con n,m cualquier entero
                {500 ,520},
                {100,120},
                {100 ,120},
                {200 ,220},
                {200 ,220},
                {2000 ,2100}};

        ColumnaApilada=new PlotBarritas(this,"Balance General","");

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
        String Acota[]={"Inversión inicial","Inversion Actual"};
        double y[][]={{100 ,120},//y[7]{3]  defina un array de 7 grupos con 3 columnas  puede ser de y[n][m] con n,m cualquier entero
                {500 ,520},
                {100,120},
                {100 ,120},
                {200 ,220},
                {200 ,220},
                {2000 ,2100}};
        ColumnaApilada100=new PlotBarritas(this,"Balance General","");

        //personalizacion de grafico
        ColumnaApilada100.ColumnaApilada100(x,y,Acota);
        ColumnaApilada100.SetHD(true);
        ColumnaApilada100.SetContorno(0);//sin contorno mayor a cero aparece el contorno por default es blanco a este metodo solo debe pasarsele un valor entre 0 y 10 mayor a eso no se toma en cuenta
        ColumnaApilada100.SetColorPila(1,255,105,180);//primera pila de columna de color hot pink
        ColumnaApilada100.SetColorPila(2,255,255,0);//segunda pila de columna de color amarillo
        pantalla.removeAllViews();
        pantalla.addView(ColumnaApilada100);
    }



}