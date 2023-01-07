package in.softment.dayplanzz.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.softment.dayplanzz.Interface.AllFavOrganiserListener;
import in.softment.dayplanzz.Interface.CheckFavoriteListener;
import in.softment.dayplanzz.Interface.EventListeners;
import in.softment.dayplanzz.Interface.LocationListener;
import in.softment.dayplanzz.MainActivity;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.Model.FavoriteModel;
import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.SignUpActivity;

public class Services {



    public static String getMonthName(int month) {
        switch (month) {
            case 1 : return "January";
            case 2 : return "February";
            case 3 : return "March";
            case 4 : return "April";
            case 5 : return "May";
            case 6 : return "Jun";
            case 7 : return "July";
            case 8 : return "August";
            case 9 : return "September";
            case 10 : return "October";
            case 11 : return "November";
            case 12 : return "December";
            default:return "Failed";
        }
    }

    public static  String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }
    public static boolean isPromoting(Date date){
        Date currentDate = new Date();
        if (currentDate.compareTo(date) < 0) {
            return true;
        }
        else {
            return false;
        }
    }



    public static Date convertTimeToDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return cal.getTime();
    }

    public static void sentPushNotificationToAdmin(Context context,String title, String message) {
        final String FCM_API = "https://fcm.googleapis.com/fcm/send";
        final String serverKey = "key=" + "BPgqt0IWfavk6qB2QF7aJhn5BagyK0Vq-nHWv5apEoJPBAyM1zzzosduKszJBfPZIQG3IduBrV0XLv4vluXUZuQ";
        final String contentType = "application/json";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notification.put("to", "/topics/admin");
            notification.put("data", notifcationBody);
        } catch (JSONException ignored) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);


    }

    public static void sentPushNotification(Context context,String title, String message, String token) {
        final String FCM_API = "https://fcm.googleapis.com/fcm/send";
        final String serverKey = "key=" + "BPgqt0IWfavk6qB2QF7aJhn5BagyK0Vq-nHWv5apEoJPBAyM1zzzosduKszJBfPZIQG3IduBrV0XLv4vluXUZuQ";
        final String contentType = "application/json";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notification.put("to", token);
            notification.put("data", notifcationBody);
        } catch (JSONException ignored) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);


    }

    public static void getAllFavOrganiser(AllFavOrganiserListener allFavOrganiserListener){

        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid())
                .collection("Favorites")
                .orderBy("organiserName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value != null && !value.isEmpty()) {
                        ArrayList<FavoriteModel> favoriteModels = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            FavoriteModel favoriteModel = documentSnapshot.toObject(FavoriteModel.class);
                            favoriteModels.add(favoriteModel);
                        }
                        allFavOrganiserListener.onCallBack(favoriteModels);
                    }
                    else {
                        allFavOrganiserListener.onCallBack(null);
                    }
                }
                else {
                    allFavOrganiserListener.onCallBack(null);
                }

            }
        });

    }
    public static void getAllCatFavOrganiser(AllFavOrganiserListener allFavOrganiserListener){

        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid())
                .collection("CategoryFavorites")
                .orderBy("organiserName").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value != null && !value.isEmpty()) {
                                ArrayList<FavoriteModel> favoriteModels = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                    FavoriteModel favoriteModel = documentSnapshot.toObject(FavoriteModel.class);
                                    favoriteModels.add(favoriteModel);
                                }
                                allFavOrganiserListener.onCallBack(favoriteModels);
                            }
                            else {
                                allFavOrganiserListener.onCallBack(null);
                            }
                        }
                        else {
                            allFavOrganiserListener.onCallBack(null);
                        }

                    }
                });

    }


    public static void addCatFavorites(String organiserName, String catId) {

        FavoriteModel favoriteModel = new FavoriteModel();
        favoriteModel.uid = catId;
        favoriteModel.organiserName = organiserName;
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("CategoryFavorites").document(catId).set(favoriteModel);
    }

    public static void removeCatFavorites(String catId){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("CategoryFavorites").document(catId).delete();
    }

    public static void checkCatFavorites(String catId, CheckFavoriteListener checkFavoriteListener){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("CategoryFavorites").document(catId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    checkFavoriteListener.onCallBack(true);
                }
                else {
                    checkFavoriteListener.onCallBack(false);
                }
            }
        });
    }


    public static void addFavorites(String organiserName, String organiserUid){

        FavoriteModel favoriteModel = new FavoriteModel();
        favoriteModel.uid = organiserUid;
        favoriteModel.organiserName = organiserName;
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(organiserUid).set(favoriteModel);

    }

    public static void removeFavorites(String organiserUid){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(organiserUid).delete();
    }

    public static void checkFavorites(String organiserUid, CheckFavoriteListener checkFavoriteListener){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(organiserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    checkFavoriteListener.onCallBack(true);
                }
                else {
                    checkFavoriteListener.onCallBack(false);
                }
            }
        });
    }



    public static void getAllEvent(EventListeners eventListener){

        final GeoLocation center = new GeoLocation(Constants.latitude, Constants.longitude);

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center,Constants.distance * 1000);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = FirebaseFirestore.getInstance().collection("Events")
                    .orderBy("hash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                EventModel eventModel = doc.toObject(EventModel.class);
                                double lat = eventModel.getLatitude();
                                double lng = eventModel.getLongitude();

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                if (distanceInM < 500000) {
                                    matchingDocs.add(doc);
                                }
                            }
                        }


                        ArrayList<EventModel> eventModels = new ArrayList<>();
                        ArrayList<EventModel> eventModels1 = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : matchingDocs) {
                            EventModel eventModel = documentSnapshot.toObject(EventModel.class);

                            if (Constants.ages.size() > 0 ) {
                                for (String age : Constants.ages) {
                                    if (eventModel.ages.contains(age)){
                                        eventModels.add(eventModel);
                                        break;
                                    }
                                }
                            }
                            else {
                                eventModels.add(eventModel);
                            }
                            eventModels1.clear();
                            eventModels1.addAll(eventModels);
                            if (Constants.payment >= 0) {

                                for (EventModel eventModel1 : eventModels) {
                                    if (Constants.payment == 0) {
                                        if (eventModel1.getEventPrice() == 0) {
                                            eventModels1.add(eventModel1);
                                        }
                                    }
                                    else if (Constants.payment == 50) {
                                        if (eventModel1.getEventPrice() > 0 && eventModel1.getEventPrice() <= 50) {
                                            eventModels1.add(eventModel1);
                                        }
                                    }
                                    else if (Constants.payment == 100) {
                                        if (eventModel1.getEventPrice() > 50 && eventModel1.getEventPrice() <= 100) {
                                            eventModels1.add(eventModel1);
                                        }
                                    }
                                    else if (Constants.payment == 200) {
                                        if (eventModel1.getEventPrice() > 100 && eventModel1.getEventPrice() <= 200) {
                                            eventModels1.add(eventModel1);
                                        }
                                    }
                                    else if (Constants.payment == 201) {
                                        if (eventModel1.getEventPrice() > 200) {
                                            eventModels1.add(eventModel1);
                                        }
                                    }
                                }
                            }

                        }

                        eventListener.onCallback(eventModels1);
                    }
                });


    }

    public static void getAllLocations(String uid, LocationListener locationListener){
        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Locations").orderBy("locationName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                  ArrayList<LocationModel> locationModels = new ArrayList<>();
                    if (error == null) {
                        if (value != null && !value.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                LocationModel locationModel = documentSnapshot.toObject(LocationModel.class);
                                locationModels.add(locationModel);
                            }
                        }

                        locationListener.onCallback(locationModels);
                    }
                    else {
                        locationListener.onCallback(locationModels);
                    }

            }
        });
    }

    public static void getAllEventForBulb(EventListeners eventListener){

        final GeoLocation center = new GeoLocation(Constants.latitude, Constants.longitude);

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center,Constants.distance * 1000);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = FirebaseFirestore.getInstance().collection("Events")
                    .orderBy("hash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                EventModel eventModel = doc.toObject(EventModel.class);
                                double lat = eventModel.getLatitude();
                                double lng = eventModel.getLongitude();

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                if (distanceInM < 500000) {
                                    matchingDocs.add(doc);
                                }
                            }
                        }


                        ArrayList<EventModel> eventModels = new ArrayList<>();


                        for (DocumentSnapshot documentSnapshot : matchingDocs) {
                            EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                            eventModels.add(eventModel);

                        }

                        eventListener.onCallback(eventModels);
                    }
                });


    }
    public static  String convertDateToEventDate(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "EEE, MMM dd";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static  String convertDateToDateAndMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "dd MMM";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }
    public static  String convertDateToMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "MMMM";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static  String convertDateToStringWithoutDash(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "dd-MMM-yyyy";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }
    public static  String convertoTimeString(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "hh:mm a";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static  String convertDateToTimeString(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "dd-MMM-yyyy, hh:mm a";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static void showCenterToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "moments ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static void logout(Context context) {


                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

    }


    public static String toUpperCase(String str) {
        if (str.isEmpty()){
            return "";
        }
        String[] names = str.trim().split(" ");
        str = "";
        for (String name : names) {
            try {
                str += name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + " ";
            }
            catch (Exception ignored){

            }
        }
     return str;
    }
    public static void showDialog(Context context,String title,String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        Activity activity = (Activity) context;
        View view = activity.getLayoutInflater().inflate(R.layout.error_message_layout, null);
        TextView titleView = view.findViewById(R.id.title);
        TextView msg = view.findViewById(R.id.message);
        titleView.setText(title);
        msg.setText(message);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (title.equalsIgnoreCase("VERIFY YOUR EMAIL")) {
                    if (context instanceof SignUpActivity) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            }
        });

        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();

        }

    }


    public static void sendMail(Context context,String name, String email, String subject, String body) {


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,"https://softment.in/php-mailer/sendmail.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Services.getCurrentUserData(context,FirebaseAuth.getInstance().getCurrentUser().getUid(),true,false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Services.getCurrentUserData(context,FirebaseAuth.getInstance().getCurrentUser().getUid(),true,false);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("appName","Day Planzz");
                params.put("name",name);
                params.put("email",email);
                params.put("subject",subject);
                params.put("body",body);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);


    }


    public static void addUserDataOnServer(Context context, UserModel userModel){

        ProgressHud.show(context,"");
        FirebaseFirestore.getInstance().collection("Users").document(userModel.getUid()).set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    if (!userModel.getUserType().equalsIgnoreCase("user")) {
                        Services.sendMail(context,userModel.getFullName(),userModel.getEmail(),"Kontot väntar","Hej! Tack för att du ansöker om ett arrangörskonto på vår plattform. För närvarande granskar vi ditt konto och vi kommer att meddela dig inom 12 timmar.\nTack");
                    }
                    else {
                        Services.getCurrentUserData(context,FirebaseAuth.getInstance().getCurrentUser().getUid(),true,false);
                    }


                }
                else {
                    Services.showDialog(context,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }



    public static void getCurrentUserData(Context context,String uid, Boolean showProgress,boolean shouldUpdate) {

        if (showProgress) {
            ProgressHud.show(context,"");
        }

        FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (showProgress) {
                    ProgressHud.dialog.dismiss();
                }

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        documentSnapshot.toObject(UserModel.class);

                        if (UserModel.data != null) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }
                        else  {
                           showCenterToast(context,"Något gick fel. Kod - 101");
                        }
                    }
                    else {
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showDialog(context,"Användaren hittades inte","Ditt konto är inte tillgängligt. Vänligen skapa ditt konto.");
                                }
                                else {
                                    showDialog(context,"FEL",task.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                }
                else {
                    Services.showDialog(context,"ERROR",task.getException().getMessage());
                }


            }
        });
    }

    public  static Date getDateFromTimestamp(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



}
