package dulinglai.android.ate.model.entities;

import dulinglai.android.ate.model.components.Listener;

import java.util.HashSet;
import java.util.Set;

public class DialogEntity extends Entity {

    private String parentActivity = "";
    private Set<Listener> posListeners = new HashSet<>();
    private Set<Listener> negListeners = new HashSet<>();
    private Set<Listener> itemListeners = new HashSet<>();

    public DialogEntity(String name) {
        super(name);
    }

    @Override
    public Entity clone() {
        DialogEntity newEntity = new DialogEntity(name);
        newEntity.addPosListeners(posListeners);
        newEntity.addNegListeners(negListeners);
        newEntity.addItemListeners(itemListeners);
        return newEntity;
    }

    @Override
    public boolean allValuesFound() {
        return !parentActivity.equals("");
    }

    /*
    Getters and setters
     */
    public String getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(String parentActivity) {
        this.parentActivity = parentActivity;
    }

    public Set<Listener> getPosListeners() {
        return posListeners;
    }

    public void addPosListener(Listener posListener) {
        this.posListeners.add(posListener);
    }

    public void addPosListeners(Set<Listener> posListeners) {
        this.posListeners.addAll(posListeners);
    }

    public Set<Listener> getNegListeners() {
        return negListeners;
    }

    public void addNegListener(Listener negListener) {
        this.negListeners.add(negListener);
    }

    public void addNegListeners(Set<Listener> negListeners) {
        this.negListeners.addAll(negListeners);
    }

    public Set<Listener> getItemListeners() {
        return itemListeners;
    }

    public void addItemListener(Listener itemListener) {
        this.itemListeners.add(itemListener);
    }

    public void addItemListeners(Set<Listener> itemListeners) {
        this.itemListeners.addAll(itemListeners);
    }
}
