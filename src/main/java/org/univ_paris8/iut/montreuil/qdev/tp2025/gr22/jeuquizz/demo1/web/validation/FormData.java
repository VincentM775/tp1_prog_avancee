package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation;

import java.util.HashMap;
import java.util.Map;

public class FormData {

    private final Map<String, String> values = new HashMap<>();

    public void set(String field, String value) {
        values.put(field, value != null ? value : "");
    }

    public String get(String field) {
        return values.getOrDefault(field, "");
    }

    public Map<String, String> getValues() {
        return values;
    }

    public static FormData fromRequest(jakarta.servlet.http.HttpServletRequest request, String... fields) {
        FormData formData = new FormData();
        for (String field : fields) {
            formData.set(field, request.getParameter(field));
        }
        return formData;
    }
}
