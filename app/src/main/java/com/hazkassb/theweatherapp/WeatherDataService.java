package com.hazkassb.theweatherapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

//    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";

    Context context;
    String cityID = "";


    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }


    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener){
        String url = queryForCityID() + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //This worked, but it didn't return the id number to MainActivity
                Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Something is wrong.", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);

        //returned an empty string ==> problem!
//        return cityID;
    }

    //Returns the query string
    private String queryForCityID() {
        return "https://www.metaweather.com/api/location/search/?query=";
    }

    private String QUERY_FOR_CITY_WEATHER_BY_ID() {
        return "https://www.metaweather.com/api/location/";
    }


    public interface ForecastByIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReortModel> weatherReortModels);
    }


    public void getCityForecastByID(String citID, final ForecastByIDResponse forecastByIDResponse){
        List<WeatherReortModel> weatherReortModels = new ArrayList<>();

        // get the json object
        String url = QUERY_FOR_CITY_WEATHER_BY_ID() + cityID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                    //get the first item in the array

                    for(int i = 0; i < consolidated_weather_list.length(); i++) {
                        WeatherReortModel one_day_weather = new WeatherReortModel();
                        JSONObject first_day_from_api = (JSONObject) consolidated_weather_list.get(i);

                        one_day_weather.setId(first_day_from_api.getInt("id"));
                        one_day_weather.setWeather_state_name((first_day_from_api.getString("weather_state_name")));
                        one_day_weather.setWeather_state_abbr(first_day_from_api.getString("weather_state_abbr"));
                        one_day_weather.setWind_direction_compass(first_day_from_api.getString("wind_direction_compass"));
                        one_day_weather.setCreated(first_day_from_api.getString("created"));
                        one_day_weather.setApplicable_date(first_day_from_api.getString("applicable_date"));
                        one_day_weather.setMin_temp(first_day_from_api.getLong("min_temp"));
                        one_day_weather.setMax_temp(first_day_from_api.getLong("max_temp"));
                        one_day_weather.setThe_temp(first_day_from_api.getLong("the_temp"));
                        one_day_weather.setWind_speed(first_day_from_api.getLong("wind_speed"));
                        one_day_weather.setWind_direction(first_day_from_api.getLong("wind_direction"));
                        one_day_weather.setAir_pressure(first_day_from_api.getLong("air_pressure"));
                        one_day_weather.setHumidity(first_day_from_api.getInt("humidity"));
                        one_day_weather.setVisibility(first_day_from_api.getLong("visibility"));
                        one_day_weather.setPredictability(first_day_from_api.getInt("predictability"));

                        weatherReortModels.add(one_day_weather);

                    }
                        forecastByIDResponse.onResponse(weatherReortModels);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

                // get the property called "consolidated_weather" which is an array


                // get each item in the array and assign it to a new WeatherReportModel object.


        MySingleton.getInstance(context).addToRequestQueue(request);

    }



    public interface GetCityForecastByNameCallBack {
        void onError(String message);
        void onResponse(List<WeatherReortModel> weatherReortModels);
    }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallBack getCityForecastByNameCallBack){
        // fetch the city id given the city name
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                // now we have the city id
                getCityForecastByID(cityID, new ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReortModel> weatherReortModels) {
                        // we have the weather report
                        getCityForecastByNameCallBack.onResponse(weatherReortModels);
                    }
                });
            }
        });


        // fetch the city forecast given the city id

    }


}
