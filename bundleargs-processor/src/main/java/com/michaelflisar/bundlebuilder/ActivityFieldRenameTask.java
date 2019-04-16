package com.michaelflisar.bundlebuilder;

class ActivityFieldRenameTask
{
    public String from;
    public String to;

    public void apply(ActivityToAnotherClassLink link)
    {
        if (link.linkElement.getSimpleName().toString().equals(from))
            link.linkAlias=to;
    }
}
