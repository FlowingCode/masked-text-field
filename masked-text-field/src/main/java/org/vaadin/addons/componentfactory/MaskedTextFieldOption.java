package org.vaadin.addons.componentfactory;

public class MaskedTextFieldOption {
    private String option;
    private String value;
    private boolean eval;

    public MaskedTextFieldOption(String option, String value) {
        this(option, value, false);
    }

    public MaskedTextFieldOption(String option, String value, boolean eval) {
        this.option = option;
        this.value = value;
        this.eval = eval;
    }

    public String getOption() {
        return option;
    }

    public String getValue() {
        return value;
    }

    public Boolean getEval() {
        return eval;
    }
}
