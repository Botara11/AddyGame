package com.kenso.addyclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.kenso.addyclient.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

public class MainActivity extends Activity {
    private TextView output;
    private Button Button5;
    private Button Button6;
    private Button Button7;
    private Button Button8;
    String Respuesta = "Hola";
    String Respuesta_anterior = "Adios";
    String Pregunta = "Pregunta";
    String Button5_text = "";
    String Button6_text = "";
    String Button7_text = "";
    String Button8_text = "";
    String Correcta;
    int flag = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.TextView01);
        Button5 = (Button) findViewById(R.id.button5);
        Button6 = (Button) findViewById(R.id.button6);
        Button7 = (Button) findViewById(R.id.button7);
        Button8 = (Button) findViewById(R.id.button8);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());

        //ejecutaCliente();

        DownloadFilesTask asyn = new DownloadFilesTask();
        //Threadpool
        asyn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        output.setText("Esperando Pregunta...");
        Button5.setText("");
        Button5.setEnabled(false);
        Button6.setEnabled(false);
        Button7.setEnabled(false);
        Button8.setEnabled(false);
        Button6.setText("");
        Button7.setText("");
        Button8.setText("");

        Button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button5action();
            }
        }
        );

        Button6.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
                                           button6action();
                                       }
                                   }
        );

        Button7.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
                                           button7action();
                                       }
                                   }
        );

        Button8.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
                                           button8action();
                                       }
                                   }
        );


    }


    public void button5action(){

        if(Button5_text.compareTo(Correcta) == 0){
            Button5.setBackgroundColor(0x32CD32);
            //Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }else{
            Button5.setBackgroundColor(0xFF0000);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }

    }


    public void button6action(){

        if(Button6_text.compareTo(Correcta) == 0){
            Button6.setBackgroundColor(0x32CD32);
            Button5.setEnabled(false);
            //Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }else{
            Button6.setBackgroundColor(0xFF0000);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }

    }


    public void button7action(){

        if(Button7_text.compareTo(Correcta) == 0){
            Button7.setBackgroundColor(0x32CD32);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            //Button7.setEnabled(false);
            Button8.setEnabled(false);
        }else{
            Button7.setBackgroundColor(0xFF0000);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }

    }


    public void button8action(){

        if(Button8_text.compareTo(Correcta) == 0){
            Button8.setBackgroundColor(0x32CD32);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            //Button8.setEnabled(false);
        }else{
            Button8.setBackgroundColor(0xFF0000);
            Button5.setEnabled(false);
            Button6.setEnabled(false);
            Button7.setEnabled(false);
            Button8.setEnabled(false);
        }

    }







    private class DownloadFilesTask extends AsyncTask<URL, Integer, Integer> {
        protected Integer doInBackground(URL... urls) {

                System.out.println("Before Server Response *******************************");
                ejecutaCliente();

            return 0;
        }
        //@Override
        protected void onProgressUpdate(Integer... progress) {
            if (flag == 0) {
                Button5.setEnabled(true);
                Button6.setEnabled(true);
                Button7.setEnabled(true);
                Button8.setEnabled(true);
                Button5.setBackgroundColor(0xC0C0C0);
                Button6.setBackgroundColor(0xC0C0C0);
                Button7.setBackgroundColor(0xC0C0C0);
                Button8.setBackgroundColor(0xC0C0C0);
            }
            output.setText(Pregunta);
            Button5.setText(Button5_text);
            Button6.setText(Button6_text);
            Button7.setText(Button7_text);
            Button8.setText(Button8_text);
            System.out.println("On progress update");

        }

        protected void onPostExecute(Long result) {
            //output.setText(Respuesta);
        }

        private void ejecutaCliente() {
            String ip="192.168.1.196";
            int puerto=10000;
            System.out.println(" socket " + ip + " " + puerto);
            while(true) {
                try {
                    Socket sk = new Socket(ip, puerto);
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sk.getOutputStream()));
                    out.write("GET / HTTP/1.1\r\n");
                    //out.newLine();
                    out.write("Host: 192.168.1.196:10000 \r\n");
                    out.write("Server: Apache/0.8.4\r\n");
                    //out.write("Content-Type: text/html\r\n");
                    //out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
                    out.newLine();
                    out.flush();

                    System.out.println("enviando...");
                    System.out.println("Hola Mundo...");
                    entrada.ready();
                    //String line = entrada.readLine();
                    //while (line!=null && !line.isEmpty()) {

                    //  System.out.println(line);
                    //line = entrada.readLine();
                    //}
                    while (entrada.readLine() != null) {
                        System.out.println("recibiendo ... " + entrada.readLine());
                        Respuesta = entrada.readLine();
                    }

                    entrada.close();
                    out.close();
                    sk.close();


                    System.out.println("Respuesta------------------ ... " + Respuesta);
                    System.out.println("Respuesta_anterior------------------ ... " + Respuesta_anterior);
                    if (Respuesta.compareTo(Respuesta_anterior) == 0) {
                        System.out.println("If*******************************");
                        flag = 1;
                    }else {

                        flag = 0;
                        System.out.println("Else*******************************");
                        if (Respuesta.compareTo("Nivea") == 0) {
                            Pregunta = "¿Qué producto se está anunciando?";
                            Button5_text = "Crema";
                            Button6_text = "Champús";
                            Button7_text = "Desodorante";
                            Button8_text = "Espuma de afeitar";
                            Correcta = "Crema";
                            System.out.println("Detectado Nivea*******************************");
                        } else if (Respuesta.compareTo("Cocacola") == 0) {
                            Pregunta = "¿Cuál es la marca de este anuncio?";
                            Button5_text = "Quttin";
                            Button6_text = "Fanta";
                            Button7_text = "Bosch";
                            Button8_text = "Cocacola";
                            Correcta = "Cocacola";
                            System.out.println("Detectado Cocacola*******************************");
                        } else if (Respuesta.compareTo("Calcedonia") == 0) {
                            Pregunta = "¿Qué anuncia esta marca?";
                            Button5_text = "Ropa interior para mujeres";
                            Button6_text = "Calzado para mujeres";
                            Button7_text = "Medias, pantis y moda playa";
                            Button8_text = "Productos de mujer en general";
                            Correcta = "Medias, pantis y moda playa";
                            System.out.println("Detectado Calcedonia*******************************");
                        } else if (Respuesta.compareTo("My song 1") == 0) {
                            Pregunta = "¿Quién es el artista de esta canción?";
                            Button5_text = "Pharrell Williams";
                            Button6_text = "Bruno Mars";
                            Button7_text = "Sting";
                            Button8_text = "Jamiroquai";
                            Correcta = "Bruno Mars";
                            System.out.println("Detectado My Song 1*******************************");
                        } else if (Respuesta.compareTo("My Song 2") == 0) {
                            Pregunta = "¿Cómo se llama esta canción?";
                            Button5_text = "Hello";
                            Button6_text = "Pink toes";
                            Button7_text = "East of Eden";
                            Button8_text = "Rolling in the deep";
                            Correcta = "East of Eden";
                            System.out.println("Detectado My Song 2*******************************");
                        }

                        Respuesta_anterior = Respuesta;
                    }
                    publishProgress(1);

                } catch (Exception e) {
                    System.out.println("error: " + e.toString());
                }
            }
        }
    }


    //private void log(String string) {
      //  output.append(string + "\n");
    //}
}