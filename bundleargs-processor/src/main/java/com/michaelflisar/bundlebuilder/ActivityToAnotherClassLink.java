package com.michaelflisar.bundlebuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class ActivityToAnotherClassLink
{
    //ex: activity.presenter.test=activity.test
    //fieldName is test
    //linkElement is presenter

    public String fieldName;
    public String linkAlias;
    public Element linkElement;

    @Override
    public String toString()
    {
        return "annotatedClass."+
                (linkAlias!=null?linkAlias:linkElement.getSimpleName())+
                (linkElement.getKind()==ElementKind.METHOD?"()":"")+
                "."+fieldName+"=annotatedClass."+fieldName+";";
    }
}
