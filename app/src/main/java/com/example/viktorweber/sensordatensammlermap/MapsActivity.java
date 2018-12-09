package com.example.viktorweber.sensordatensammlermap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
        LocationManager locman;
        private GoogleMap mMap;
        EditText editText;
        EditText editText2;
        Button button;
        private GraphView graphAcc;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            locman = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LocationListener loclist = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng position = new LatLng(latitude, longitude);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(position).visible(true));


                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }
                };
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclist);
            }




        }



        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            locman = (LocationManager) getSystemService(LOCATION_SERVICE);
            read();
            //readground(); Diese Funktion verursacht fehler!!
            double long2 ;
            double langs2 ;
            double messtime;
            editText = findViewById(R.id.editText);
            editText2 = findViewById(R.id.editText2);
            button = findViewById(R.id.button);
            setUpGraphView();
            xywerte.setColor(Color.RED);
            //Ausblenden von Edittext etc;
            editText.setVisibility(View.INVISIBLE);
            editText2.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);

            double teil2;
            double latlngnew2;
            double latlngnew;


            //Groundtruth werte

            LatLng[] tlatlng = new LatLng[8];

            //Das Hier muss rein wen das auslesen funktioniert von groundtruth!!!!
            /*
            for (int i = 0;i<8;i++) {
                tlatlng[i] = new LatLng(groundtruthe.get(i).getLangatitude(), groundtruthe.get(i).getLongtitude());
            }
           */
// Zurzeit gebe ich die groundtruh werte per Hand ein, da das auslesen noch fehler macht
            tlatlng[0]=new LatLng(51.44800,7.27084);
            tlatlng[1]=new LatLng(51.44828,7.27171);
            tlatlng[2]=new LatLng(51.44796,7.27199);
            tlatlng[3]=new LatLng(51.44783,7.27202);
            tlatlng[4]=new LatLng(51.44733,7.27246);
            tlatlng[5]=new LatLng(51.44714,7.27223);
            tlatlng[6]=new LatLng(51.44693,7.27236);
            tlatlng[7]=new LatLng(51.44674,7.27176);

            final float[] distanz= new float[liste.size()];
            float[] distanz1 = new float[19];

            //Groundtruth zeitwerte
            double[] groundtruth = new double[8];
            /*
            //Das Hier muss rein wen das auslesen funktioniert von groundtruth!!!!
            for(int s=0;s<8;s++){
            groundtruth[s]= groundtruthe.get(s).getTimestamp();
            }
            */

            // Zurzeit gebe ich die groundtruh werte per Hand ein, da das auslesen noch fehler macht. Hier sind die Timestamps
            groundtruth[0]= 606372;
            groundtruth[1]= 644799;
            groundtruth[2]= 669833;
            groundtruth[3]= 680238;
            groundtruth[4]= 713104;
            groundtruth[5]= 730543;
            groundtruth[6]= 744228;
            groundtruth[7]= 768282;


// Erstellen einer Polyline um den Laufweg dar zu stellen
            mMap.addPolyline(new PolylineOptions().add(tlatlng[0]).add(tlatlng[1]).add(tlatlng[2]).add(tlatlng[3]).add(tlatlng[4]).add(tlatlng[5]).add(tlatlng[6]).add(tlatlng[7]));

           for(int i = 0 ;i<liste.size();i++) {
                //Werte von LangLong holen und Zeitstempel
               long2 = liste.get(i).getLangatitude();
               langs2 = liste.get(i).getLongtitude();
               messtime =  liste.get(i).getTimestamp();

                //Ueberpruefen ob Werte im Messbereich liegen
               if(messtime>groundtruth[7]){
                   break;
               }
               if(messtime<groundtruth[0]){
                   continue;
               }
               //Neu geogordinaten Anlegen
               LatLng position2 = new LatLng(langs2,long2);
               //Variable Initialisieren fuer die Uberpruefung und Berechnung der Interpolation
               LatLng position3=new LatLng(0,0);
               // Bestimmen zwischen welchen Punkten der Messpunkt liegt
                int werte=0;
               for(int s=0;s<7;s++){
                   if (messtime>groundtruth[s])
                    werte=s;
               }

    // Interpolation
                   if (messtime > groundtruth[werte] && messtime < groundtruth[werte+1]) {
                       double oben = messtime - groundtruth[werte];
                       double verhalt = groundtruth[werte+1] - groundtruth[werte];
                       teil2 = oben / verhalt;
                       latlngnew2 = tlatlng[werte].longitude + (tlatlng[werte+1].longitude - tlatlng[werte].longitude) * teil2;
                       latlngnew = tlatlng[werte].latitude + (tlatlng[werte+1].latitude - tlatlng[werte].latitude) * teil2;
                       position3 = new LatLng(latlngnew, latlngnew2);
                   } else {
                       position3 = new LatLng(0, 0);
                   }

// Berechnen und speichern der Distanzen in distanz
               Location.distanceBetween(position2.latitude,position2.longitude,position3.latitude,position3.longitude,distanz1);
               distanz[i]=distanz1[0];
               //Interpolierte Punkte
               mMap.addCircle(new CircleOptions().center(position3).radius(0.5).strokeColor(Color.GREEN).fillColor(Color.BLUE));
               //Gemesene Punkte
               mMap.addCircle(new CircleOptions().center(position2).radius(0.5).strokeColor(Color.RED).fillColor(Color.BLUE));
           }

           /*
           // Distanz werte abspeichern als csv
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                editText.setText(Float.toString(distanz[4]));
                }
            });


           // Anzeigen von distanz werten
            editText.setText(Float.toString(distanz[17]));
            editText2.setText(Float.toString(distanz[20]));
            */
            //Graphview erstellen
//Nullen raus filtern
            int ungleich0 = 0;
            for(int cs=0;cs<distanz.length;cs++){
                if(distanz[cs]>0){
                    ungleich0++;
                }
            }
            float[] distanzohnenull = new float[ungleich0];
            ungleich0=0;
            for(int cs=0;cs<distanz.length;cs++){
                if(distanz[cs]>0){
                    distanzohnenull[ungleich0]=distanz[cs];
                    ungleich0++;
                }
            }
//Ende von Funktion nullen raus filtern

            datapointadding(bubblesort(distanzohnenull));
            graphAcc.addSeries(xywerte);
            graphAcc.addSeries(xywertePunkte);
            xywertePunkte.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(1);
                    canvas.drawLine(x-20, y-20, x+20, y+20, paint);
                    canvas.drawLine(x+20, y-20, x-20, y+20, paint);
                }
            });


        }









    private void setUpGraphView(){
        graphAcc = (GraphView) findViewById(R.id.graphAcc);
        graphAcc.getViewport().setYAxisBoundsManual(true);
        graphAcc.getViewport().setMinY(0);
        graphAcc.getViewport().setMaxY(1);
        graphAcc.getViewport().setMinX(0);
        graphAcc.getViewport().setMaxX(50);
        graphAcc.getViewport().setXAxisBoundsManual(true);
    }

    //
   private LineGraphSeries<DataPoint> xywerte = new LineGraphSeries<DataPoint>();
        private PointsGraphSeries xywertePunkte = new PointsGraphSeries<>();

    private void datapointadding(float[] daten){
            for (int i =0;i<daten.length;i++) {
                float s = i;
                DataPoint dp = new DataPoint(daten[i],s/daten.length);
                xywerte.appendData(dp,false,200);
                xywertePunkte.appendData(dp,false,200);
    }
    }

    private List<Messpunkte> liste = new ArrayList<>();
public void read(){
            InputStream is = getResources().openRawResource(R.raw.outdoorroute1round1gpsfile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line ="";


try {
    reader.readLine();
    while ((line = reader.readLine()) != null) {
        Messpunkte messpunkte = new Messpunkte();

         messpunkte.setLongtitude(extractLatInDoubleFormat(line));
         messpunkte.setLangatitude(extractLonInDoubleFormat(line));
         messpunkte.setTimestamp(extractTimestamp(line));
         liste.add(messpunkte);
    }
} catch (IOException e1) {
    Log.wtf("HieristderFehler"+ line, e1);
    e1.printStackTrace();
}

}

  public static float[] bubblesort(float[] zusortieren) {
		float temp;
		for(int i=1; i<zusortieren.length; i++) {
			for(int j=0; j<zusortieren.length-i; j++) {
				if(zusortieren[j]>zusortieren[j+1]) {
					temp=zusortieren[j];
					zusortieren[j]=zusortieren[j+1];
					zusortieren[j+1]=temp;
				}

			}
		}
		return zusortieren;
	}

    public static double extractLatInDoubleFormat(String line){
        String[] splitUpLine = line.split(",");
        int hour = Integer.parseInt(splitUpLine[1].substring(splitUpLine[1].indexOf("N") + 2, splitUpLine[1].indexOf("°")));
        double minute = Double.parseDouble(splitUpLine[1].substring(splitUpLine[1].indexOf("'") - 2, splitUpLine[1].indexOf("'")));
        int secondPreComma = Integer.parseInt(splitUpLine[1].substring(splitUpLine[1].indexOf("'") + 2, splitUpLine[1].length()));
        int secondPostComma = Integer.parseInt(splitUpLine[2].substring(0, splitUpLine[2].length() - 1));
        double minConverted = minute / 60;
        double secConverted = Double.parseDouble(secondPreComma + "." + secondPostComma) / 3600;
        double result = hour + minConverted + secConverted;
        Log.d("TEST", "result in ConvLat: " + result);
        return result;
    }
    public static double extractLonInDoubleFormat(String line){
        String[] splitUpLine = line.split(",");
        int hour = Integer.parseInt(splitUpLine[3].substring(splitUpLine[3].indexOf("E") + 2, splitUpLine[3].indexOf("°")));
        double minute = Double.parseDouble(splitUpLine[3].substring(splitUpLine[3].indexOf("'") - 2, splitUpLine[3].indexOf("'")));
        int secondPreComma = Integer.parseInt(splitUpLine[3].substring(splitUpLine[3].indexOf("'") + 2, splitUpLine[3].length()));
        int secondPostComma = Integer.parseInt(splitUpLine[4].substring(0, splitUpLine[4].length() - 1));
        double minConverted = minute / 60;
        double secConverted = Double.parseDouble(secondPreComma + "." + secondPostComma) / 3600;
        double result = hour + minConverted + secConverted;
        Log.d("TEST", "result in ConvLong: " + result);

        return result;
    }

    public static double extractTimestamp(String line){
        String[] splitUpLine = line.split(",");
        String zeit = splitUpLine[0].substring(6,splitUpLine[0].length());
        double result = Double.parseDouble(zeit);
        return result;
    }


    private  List<Groundtruth> groundtruthe = new ArrayList<>();
    public void readground (){
        //Hier muss man per Hand die passende Groundtruhdatei angeben
        InputStream is = getResources().openRawResource(R.raw.outdoorroute1round1gtwpswithts);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line ="";


        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Groundtruth groundtruth = new Groundtruth();

                groundtruth.setLongtitude(extractLatinDoubleFormatgroundtruth(line));
                groundtruth.setLangatitude(extractLoninDoubleFormatgroundtruth(line));
                groundtruth.setTimestamp(extractTimestampVonGroundtruth(line));
                groundtruthe.add(groundtruth);
            }
        } catch (IOException e1) {
            Log.wtf("HieristderFehler"+ line, e1);
            e1.printStackTrace();
        }


    }

    public static double extractTimestampVonGroundtruth(String line){
    String[] splitUpLine = line.split(",");
    String zeit = splitUpLine[1].substring(6,splitUpLine[0].length());
    double result =Double.parseDouble(zeit);
    return  result;
    }

    public static double extractLoninDoubleFormatgroundtruth(String line){
        String[] splitUpLine = line.split(",");
        String lon = splitUpLine[2].substring(0,1);
        String lonhinten= splitUpLine[2].substring(3,7);
    double result = Double.parseDouble(lon +"."+lonhinten);
    return result;
    }

    public static double extractLatinDoubleFormatgroundtruth(String line){
        String[] splitUpLine = line.split(";");
        String lat = splitUpLine[3].substring(0,0);
        String lathinten = splitUpLine[3].substring(2,6);
        double result = Double.parseDouble(lat+"."+lathinten);
        return result;
    }


}

