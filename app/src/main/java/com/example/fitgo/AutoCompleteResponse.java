package com.example.fitgo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para la respuesta del endpoint /v2/search/instant de Nutritionix.
 * Contiene dos listas: common (alimentos genéricos) y branded (marcas).
 * Para simplificar, las unimos todas en `items`.
 */
public class AutoCompleteResponse {
    @SerializedName("common")
    public List<Item> common;

    @SerializedName("branded")
    public List<Item> branded;

    /**
     * Concatena branded + common en una sola lista.
     */
    public List<Item> getItems() {
        List<Item> out = new ArrayList<>();
        if (common   != null) out.addAll(common);
        if (branded  != null) out.addAll(branded);
        return out;
    }

    public static class Item {
        @SerializedName("food_name")
        public String food_name;
    }
}
