package com.example.fitgo;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Exercise {
    private String id;
    private String name;
    private String bodyPart;
    private String equipment;
    private String target;
    @SerializedName("gifUrl")
    private String gifUrl;
    private String nameEs;
    private String bodyPartEs;
    private String equipmentEs;
    private String targetEs;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }
    public String getNameEs() { return nameEs; }
    public void setNameEs(String nameEs) { this.nameEs = nameEs; }
    public String getBodyPartEs() { return bodyPartEs; }
    public void setBodyPartEs(String bodyPartEs) { this.bodyPartEs = bodyPartEs; }
    public String getEquipmentEs() { return equipmentEs; }
    public void setEquipmentEs(String equipmentEs) { this.equipmentEs = equipmentEs; }
    public String getTargetEs() { return targetEs; }
    public void setTargetEs(String targetEs) { this.targetEs = targetEs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise e = (Exercise) o;
        return Objects.equals(id, e.id) &&
                Objects.equals(name, e.name) &&
                Objects.equals(bodyPart, e.bodyPart) &&
                Objects.equals(equipment, e.equipment) &&
                Objects.equals(target, e.target) &&
                Objects.equals(gifUrl, e.gifUrl) &&
                Objects.equals(nameEs, e.nameEs) &&
                Objects.equals(bodyPartEs, e.bodyPartEs) &&
                Objects.equals(equipmentEs, e.equipmentEs) &&
                Objects.equals(targetEs, e.targetEs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bodyPart, equipment, target, gifUrl, nameEs, bodyPartEs, equipmentEs, targetEs);
    }
}
