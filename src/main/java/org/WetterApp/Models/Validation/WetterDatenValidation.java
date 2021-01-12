package org.WetterApp.Models.Validation;

import org.WetterApp.Models.InvalidWetterDatenModel;
import org.WetterApp.Models.WetterDatenModel;
import org.apache.commons.beanutils.BeanUtils;

public class WetterDatenValidation {

    public static WetterDatenModel validate(WetterDatenModel model)
    {
        if(model.getTempInC() > -20 && model.getTempInC() < 50 &&
            model.getLuftDruck() < 1300 && model.getLuftDruck() > 800 &&
            model.getLuftFeuchtigkeit() < 95 && model.getLuftFeuchtigkeit() > 20 &&
            model.getCo2() > 1 & model.getCo2() < 5 &&
            model.getWindGeschw() >= 0 && model.getWindGeschw() < 150)
        return model;
        else {
            InvalidWetterDatenModel invalidModel = new InvalidWetterDatenModel();
            try{
                BeanUtils.copyProperties(invalidModel,model);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
            return invalidModel;
        }
    }
}
