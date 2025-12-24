package es.tew.presentation;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.Locale;

@Named("settings")
@SessionScoped
public class BeanSettings implements Serializable {

    private static final Locale ENGLISH = new Locale("en");
    private static final Locale SPANISH = new Locale("es");
    private Locale locale = SPANISH;

    public Locale getLocale() {
        return locale;
    }

    public void setSpanish(ActionEvent event) {
        locale = SPANISH;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    public void setEnglish(ActionEvent event) {
        locale = ENGLISH;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}