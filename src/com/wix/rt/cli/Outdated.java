//package com.wix.rt.cli;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.annotations.SerializedName;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//
//public class Outdated {
//    @SerializedName("react-templates")
//    public OutdatedClass rt;
//
//    public static Outdated parseNpmOutdated(String json) {
//        GsonBuilder builder = new GsonBuilder();
//        // builder.registerTypeAdapterFactory(adapter);
//        Gson g = builder.setPrettyPrinting().create();
//        Type listType = new TypeToken<Outdated>() {}.getType();
//        return g.fromJson(json, listType);
//    }
//
//    public static class OutdatedClass {
//        public String current;
//        public String wanted;
//        public String latest;
//        public String location;
//    }
//}
