package org.vaadin.addons.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.addons.componentfactory.MaskedTextField;
import org.vaadin.addons.componentfactory.MaskedTextFieldOption;

@Route(value = "")
@Theme(value = Lumo.class)
@NpmPackage(value = "@vaadin-component-factory/vcf-masked-text-field", version = "0.1.4")
@JsModule("@vaadin-component-factory/vcf-masked-text-field/src/vcf-masked-text-field.js")
public class CustomComponentView extends VerticalLayout {
    public CustomComponentView() {
        HorizontalLayout exp1 = new HorizontalLayout(new Label("Regular text field : "), new TextField());
        exp1.addClassName("input-masking-experiment");
        add(exp1);

        HorizontalLayout exp2 = new HorizontalLayout(
                new Label("Masking text -> +{7}(000)000-00-00 : "),
                new MaskedTextField(new MaskedTextFieldOption("mask", "+{7}(000)000-00-00"))
        );
        exp2.addClassName("input-masking-experiment");
        add(exp2);

        MaskedTextField maskedTextField3 = new MaskedTextField(
                new MaskedTextFieldOption("mask", "000000 0"),
                new MaskedTextFieldOption("lazy", "false", true)
        );

        maskedTextField3.setMaskedValue("123");

        HorizontalLayout exp3 = new HorizontalLayout(new Label("Masking text (lazy=false) -> 000000 0 : "), maskedTextField3);
        exp3.addClassName("input-masking-experiment");
        add(exp3);

        MaskedTextField maskedTextField4 = new MaskedTextField(
                new MaskedTextFieldOption("mask", "000000 0"),
                new MaskedTextFieldOption("lazy", "false", true),
                new MaskedTextFieldOption("overwrite", "true", true)
        );

        HorizontalLayout exp4 = new HorizontalLayout(
                new Label("Masking text (lazy=false, overwrite=true) -> 000000 0 : "),
                maskedTextField4,
                new Button("Convert into number between 0 and 1000", clickEvent -> {
                    maskedTextField4.setMaskOptions(
                            new MaskedTextFieldOption("mask", "Number", true),
                            new MaskedTextFieldOption("min", "0", true),
                            new MaskedTextFieldOption("max", "1000", true)
                    );
                    maskedTextField4.updateMaskOptions();
                })
        );
        exp4.addClassName("input-masking-experiment");
        add(exp4);

        Label val4 = new Label("");
        Label val5 = new Label("");
        Label val6 = new Label("");
        HorizontalLayout exp41 = new HorizontalLayout(
                new Button("Get value =>", clickEvent -> {
                    val4.setText(maskedTextField4.getUnmaskedValue());
                }),
                val4, val5, val6
        );
        maskedTextField4.addImaskAcceptEventHandler(event -> {
            val5.setText(maskedTextField4.getMaskedValue());
            val6.setText(maskedTextField4.getUnmaskedValue());
        });
        maskedTextField4.addImaskCompleteEventHandler(event -> {
            getElement().executeJs("console.log('EVENT: completed')");
        });
        add(exp41);

        HorizontalLayout exp42 = new HorizontalLayout(
                new Button("Set unm. value = 123", clickEvent -> maskedTextField4.setMaskedValue("123"))
        );
        add(exp42);

        MaskedTextField maskedTextField5 = new MaskedTextField(new MaskedTextFieldOption("mask", "{V}00{12}000000000"));
        HorizontalLayout exp5 = new HorizontalLayout(
                new Label("Suggested test #1 - {V}00{12}000000000"),
                maskedTextField5,
                new Button("Console.log value unmasked", clickEvent -> getElement().executeJs("console.log($0)", maskedTextField5.getUnmaskedValue()))
        );
        add(exp5);

        MaskedTextField maskedTextField6 = new MaskedTextField(new MaskedTextFieldOption("mask", "aa00 **** 0000 0000 [0000] [0000] [0000]"));
        HorizontalLayout exp6 = new HorizontalLayout(
                new Label("Suggested test #2 - aa00 **** 0000 0000 [0000] [0000] [0000]"),
                maskedTextField6,
                new Button("Console.log value unmasked", clickEvent -> getElement().executeJs("console.log($0)", maskedTextField6.getUnmaskedValue()))
        );
        add(exp6);
    }

}
