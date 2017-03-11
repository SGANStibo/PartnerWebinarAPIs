package com.webinar.extensionapidemo.editorfield;

import com.stibo.core.domain.Node;
import com.stibo.framework.Plugin;
import com.stibo.portal.api.component.context.ComponentContext;
import com.stibo.portal.api.component.noparameters.NoParameters;
import com.stibo.portal.api.widget.Widget;
import com.stibo.portal.componenttype.field.EditorField;
import com.stibo.portal.componenttype.field.controller.FieldController;
import com.stibo.portal.componenttype.field.controller.store.StoreEvent;
import com.stibo.portal.componenttype.field.controller.store.StoreListener;
import com.stibo.portal.event.change.event.ChangeEvent;
import com.stibo.portal.event.change.listener.ChangeListener;
import com.stibo.portal.widget.element.style.Stylist;
import com.stibo.portal.widget.formfield.FormField;
import com.stibo.portal.widget.panel.flow.FlowPanel;
import com.stibo.portal.widget.popup.notification.Notification;
import com.stibo.portal.widget.text.label.Label;
import com.stibo.portal.widget.text.textbox.TextBox;

@Plugin(id = "RetailProductNameEditorField")
public class RetailProductNameEditorField implements EditorField<NoParameters, Node> {
    @Override
    public Widget createUi(final ComponentContext<NoParameters, Node, FieldController> context) {
        //Get the node we're running on --- but remember, in "Design mode", it doesn't exist, and will be null!
        final Node node = context.getSelection();

        FlowPanel panel = new FlowPanel();

        //Get the current name
        String retailProductNameCurrentValue;
        if (node != null) {
            retailProductNameCurrentValue = node.getValue("RetailProductName").getSimpleValue();
        } else {
            retailProductNameCurrentValue = "";
        }

        //And put it in the text box
        final TextBox textBox = new TextBox();
        textBox.setText(retailProductNameCurrentValue);
        panel.add(textBox);
        Stylist.setInlineStyle(textBox, "display", "block");

        //Create the labe we'll use for the error (if any)
        final Label errorLabel = new Label();
        panel.add(errorLabel);
        Stylist.setInlineStyle(errorLabel, "display", "none");
        Stylist.setInlineStyle(errorLabel, "color", "red");

        //Copy to final variable, to make usable inside anonymous class
        final String finalRetailProductNameCurrentValue = retailProductNameCurrentValue;
        textBox.addChangeListener(new ChangeListener() {
            @Override
            public void onChanged(ChangeEvent event) {
                //Do the checks -
                String retailProductNameNewValue = textBox.getText();

                //Mark as dirty (= allow the user to try to press the "Save"-button) on the WebUI, if we've changed the value
                if(finalRetailProductNameCurrentValue != null && retailProductNameNewValue != null) {
                    context.getController().markDirty(!retailProductNameNewValue.equals(finalRetailProductNameCurrentValue));
                }

                //But only mark valid if IsRetailProduct is "Yes" (or we want to remove the value)
                if(node.getValue("IsRetailProduct").getSimpleValue().equals("Yes") || retailProductNameNewValue == null || retailProductNameNewValue.equals("")) {
                    context.getController().markValid(true);
                    Stylist.setInlineStyle(errorLabel, "display", "none");
                    context.getController().markDirty(true);

                } else {
                    errorLabel.setText("Uanble to set RetailProductName - IsRetailProduct for the node is not 'Yes'");
                    Stylist.setInlineStyle(errorLabel, "display", "inline-block");

                    context.getController().markValid(false);
                }
            }
        });

        context.getController().addStoreListener(new StoreListener() {
            @Override
            public void store(StoreEvent event) {
                try {
                    node.getValue("RetailProductName").setSimpleValue(textBox.getText());
                } catch (com.stibo.core.domain.ValidatorException e) {
                    Notification notification = new Notification("Unable to set value ", e.getLocalizedMessage());
                    notification.show();
                }
            }
        });

        return new FormField("Retail Name", panel);
    }

}
