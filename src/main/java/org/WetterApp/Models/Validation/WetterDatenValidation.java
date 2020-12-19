package org.WetterApp.Models.Validation;

import org.WetterApp.Models.IWetterDatenModel;

public interface WetterDatenValidation {

    default IWetterDatenModel validate(IWetterDatenModel model){
        return model;
    };
}
