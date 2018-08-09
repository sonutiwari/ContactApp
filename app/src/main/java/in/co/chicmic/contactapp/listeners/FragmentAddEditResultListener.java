package in.co.chicmic.contactapp.listeners;

import in.co.chicmic.contactapp.dataModels.Person;

public interface FragmentAddEditResultListener {
    void setData(Person pPerson, boolean pIsEdit);
}
