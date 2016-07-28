package com.senac.wir.converter;

import java.text.NumberFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "money")
public class Money implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent uiComponent, String salary) {
		return Double.parseDouble(salary);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent uiComponent, Object formatedSalary) {
		Double salary = (Double) formatedSalary;
		return NumberFormat.getCurrencyInstance().format(salary);
	}
}