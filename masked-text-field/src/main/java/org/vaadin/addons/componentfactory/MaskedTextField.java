package org.vaadin.addons.componentfactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@NpmPackage(value = "@vaadin-component-factory/vcf-masked-text-field", version = "0.1.7")
@JsModule("@vaadin-component-factory/vcf-masked-text-field/src/vcf-masked-text-field.js")
@Tag("vcf-masked-text-field")
@Uses(TextField.class)
public class MaskedTextField extends TextField {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, MaskedTextFieldOption> maskOptions;

    private String maskedValue;
    private String unmaskedValue;
    private TextFieldValidationSupport validationSupport;
    private ValueChangeMode currentMode;
    private int valueChangeTimeout = DEFAULT_CHANGE_TIMEOUT;
    
     public MaskedTextField(Map<String, MaskedTextFieldOption> maskOptions) {
        super();
        maskedValue = "";
        unmaskedValue = "";
        this.maskOptions = maskOptions;
        
        setInvalid(false);
        setValueChangeMode(ValueChangeMode.ON_CHANGE);
    }

    public MaskedTextField(MaskedTextFieldOption... options) {
        this(new HashMap<>());
        for(MaskedTextFieldOption opt : options) {
            this.maskOptions.put(opt.getOption(), opt);
        }
    }

    public Map<String, Object> getMaskOptions() {
        return new HashMap<>(this.maskOptions);
    }

    public void setMaskOptions(Map<String, MaskedTextFieldOption> maskOptions) {
        this.maskOptions = maskOptions;
    }

    public void setMaskOptions(MaskedTextFieldOption... options) {
        this.maskOptions.clear();
        for(MaskedTextFieldOption opt : options) {
            this.maskOptions.put(opt.getOption(), opt);
        }
    }

    public void putMaskOptions(MaskedTextFieldOption... options) {
        for(MaskedTextFieldOption opt : options) {
            this.maskOptions.put(opt.getOption(), opt);
        }
    }

    public void clearMaskOptions(String... keys) {
        for(String key : keys) {
            if (maskOptions.containsKey(key)) {
                maskOptions.remove(key);
            }
        }
    }

    public void updateMaskOptions() {
        try {
            getElement().executeJs("$1.updateIMaskOptions($0)", objectMapper.writeValueAsString(maskOptions.values().toArray()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("updateMaskOptions threw JsonProcessingException:", e);
        }
    }

    @ClientCallable
    public void _imaskAccepted(String data) {
        JSONObject dataObj = new JSONObject(data);
        this.setUnmaskedValue(dataObj.getString("unmaskedValue"), true);
        this.setMaskedValue(dataObj.getString("maskedValue"), true);
        this.setModelValue(dataObj.getString("maskedValue"), true);
        getEventBus().fireEvent(new ImaskAcceptEvent(this, true, maskedValue, unmaskedValue));
    }

    @ClientCallable
    public void _imaskCompleted(String data) {
        JSONObject dataObj = new JSONObject(data);
        this.setUnmaskedValue(dataObj.getString("unmaskedValue"), true);
        this.setMaskedValue(dataObj.getString("maskedValue"), true);
        this.setModelValue(dataObj.getString("maskedValue"), true);
        getEventBus().fireEvent(new ImaskCompleteEvent(this, true, maskedValue, unmaskedValue));
    }

    @ClientCallable
    public void _enterPressed(String data) {
        JSONObject dataObj = new JSONObject(data);
        this.setUnmaskedValue(dataObj.getString("unmaskedValue"), true);
        this.setMaskedValue(dataObj.getString("maskedValue"), true);
        getEventBus().fireEvent(new EnterPressEvent(this, true, maskedValue, unmaskedValue));
    }

    public class ImaskAcceptEvent extends ImaskEvent {
        public ImaskAcceptEvent(MaskedTextField source, boolean fromClient, String maskedValue, String unmaskedValue) {
            super(source, fromClient, maskedValue, unmaskedValue);
        }
    }

    public class ImaskCompleteEvent extends ImaskEvent {
        public ImaskCompleteEvent(MaskedTextField source, boolean fromClient, String maskedValue, String unmaskedValue) {
            super(source, fromClient, maskedValue, unmaskedValue);
        }
    }

    public class ImaskEvent extends ComponentEvent<MaskedTextField> {
        private String maskedValue;
        private String unmaskedValue;

        public ImaskEvent(MaskedTextField source, boolean fromClient, String maskedValue, String unmaskedValue) {
            super(source, fromClient);
            this.maskedValue = maskedValue;
            this.unmaskedValue = unmaskedValue;
        }

        public String getMaskedValue() {
            return maskedValue;
        }

        public String getUnmaskedValue() {
            return unmaskedValue;
        }
    }

    public class EnterPressEvent extends ImaskEvent {
        public EnterPressEvent(MaskedTextField source, boolean fromClient, String maskedValue, String unmaskedValue) {
            super(source, fromClient, maskedValue, unmaskedValue);
        }
    }

    @FunctionalInterface
    public interface ImaskEventHandler {
        void handle(ImaskEvent imaskEvent);
    }

    public Registration addImaskAcceptEventHandler(ImaskEventHandler handler) {
        return addListener(ImaskAcceptEvent.class, event -> {
            handler.handle(event);
        });
    }

    public Registration addImaskCompleteEventHandler(ImaskEventHandler handler) {
        return addListener(ImaskCompleteEvent.class, event -> {
            handler.handle(event);
        });
    }

    public Registration addEnterPressEventHandler(ImaskEventHandler handler) {
        return addListener(EnterPressEvent.class, event -> {
            handler.handle(event);
        });
    }

    public void setUnmaskedValue(String value) {
        this.setUnmaskedValue(value, false);
    }

    public void setMaskedValue(String value) {
        this.setMaskedValue(value, false);
    }

    public String getMaskedValue() {
        return maskedValue;
    }

    public String getUnmaskedValue() {
        return unmaskedValue;
    }

    @Override
    public String getValue() {
        return this.getMaskedValue();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        try {
            getElement().executeJs("$1.init($0);", objectMapper.writeValueAsString(maskOptions.values().toArray()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("onAttach threw JsonProcessingException:", e);
        }
        if(!maskedValue.isEmpty()) setMaskedValue(maskedValue, false);
        if(!unmaskedValue.isEmpty()) setMaskedValue(unmaskedValue, false);
        
        FieldValidationUtil.disableClientValidation(this);
    }

    @Override
    public void setValue(String value) {
        String notNullValue = value == null ? "" : value;
        setMaskedValue(notNullValue, true);
        super.setValue(notNullValue);
    }

    private synchronized void setUnmaskedValue(String value, boolean internal) {
        this.unmaskedValue = value;
        if(!internal && isAttached()) {
            // Send to client
            getElement().executeJs("$1.setUnmaskedValue($0)", value);
        }
    }

    private synchronized void setMaskedValue(String value, boolean internal) {
        this.maskedValue = value;
        if(!internal && isAttached()) {
            // Send to client
            getElement().executeJs("$1.setMaskedValue($0);", value);
        }
    }
    
    /**
     * Maximum number of characters (in Unicode code points) that the user can
     * enter.
     *
     * @param maxLength
     *            the maximum length
     */
    public void setMaxLength(int maxLength) {
        super.setMaxlength(maxLength);
        getValidationSupport().setMaxLength(maxLength);
    }

    /**
     * Minimum number of characters (in Unicode code points) that the user can
     * enter.
     *
     * @param minLength
     *            the minimum length
     */
    public void setMinLength(int minLength) {
        super.setMinlength(minLength);
        getValidationSupport().setMinLength(minLength);
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequired(required);
        getValidationSupport().setRequired(required);
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
        getValidationSupport().setPattern(pattern);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        super.setRequiredIndicatorVisible(requiredIndicatorVisible);
        getValidationSupport().setRequired(requiredIndicatorVisible);
    }

    /**
     * Performs server-side validation of the current value. This is needed
     * because it is possible to circumvent the client-side validation
     * constraints using browser development tools.
     */
    @Override
    protected void validate() {
        setInvalid(getValidationSupport().isInvalid(getValue()));
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * The default value is {@link ValueChangeMode#ON_CHANGE}.
     */
    @Override
    public ValueChangeMode getValueChangeMode() {
        return currentMode;
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        currentMode = valueChangeMode;
        setSynchronizedEvent(
                ValueChangeMode.eventForMode(valueChangeMode, "value-changed"));
        applyChangeTimeout();
    }
    
    @Override
    public void setValueChangeTimeout(int valueChangeTimeout) {
        this.valueChangeTimeout = valueChangeTimeout;
        applyChangeTimeout();
    }

    @Override
    public int getValueChangeTimeout() {
        return valueChangeTimeout;
    }

    private void applyChangeTimeout() {
        ValueChangeMode.applyChangeTimeout(getValueChangeMode(),
                getValueChangeTimeout(), getSynchronizationRegistration());
    }
    
    private TextFieldValidationSupport getValidationSupport() {
      if (validationSupport == null) {
          validationSupport = new TextFieldValidationSupport(this);
      }
      return validationSupport;
    }
    
}
