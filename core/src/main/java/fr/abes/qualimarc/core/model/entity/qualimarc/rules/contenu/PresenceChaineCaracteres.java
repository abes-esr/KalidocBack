package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumTypeVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe qui définie une règle permettant de tester la présence d'une zone et sous-zone ainsi que la présence,
 * la position ou la conformité d'une ou plusieurs chaines de caractères dans une sous-zone.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_PRESENCECHAINECARACTERES")
public class PresenceChaineCaracteres extends SimpleRule implements Serializable {

    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;

    @Column(name = "ENUM_TYPE_DE_VERIFICATION")
    @NotNull
    private EnumTypeVerification enumTypeDeVerification;

    @OneToMany(mappedBy = "presenceChaineCaracteres", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChaineCaracteres> listChainesCaracteres;

    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, EnumTypeVerification enumTypeDeVerification) {
        super(id, zone);
        this.sousZone = sousZone;
        this.enumTypeDeVerification = enumTypeDeVerification;
        this.listChainesCaracteres = new LinkedList<>();
    }

    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, EnumTypeVerification enumTypeDeVerification, List<ChaineCaracteres> listChainesCaracteres) {
        super(id, zone);
        this.sousZone = sousZone;
        this.enumTypeDeVerification = enumTypeDeVerification;
        this.listChainesCaracteres = listChainesCaracteres;
    }

    public void addChaineCaracteres(ChaineCaracteres chaine) {
        this.listChainesCaracteres.add(chaine);
    }

    /**
     * Méthode qui vérifie la présence ou la position d'une ou plusieurs chaine.s de caractères dans une sous-zone d'une notice
     * @param noticeXml notice sur laquelle va être testé la règle
     * @return boolean
     */
    @Override
    public boolean isValid(NoticeXml noticeXml) {
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());

        // création du boolean de résultat
        boolean isOk = false;

        // pour chaque occurence de la zone
        for (Datafield zone : zones) {

            // pour chaque occurence de la sous-zone
            for (SubField subField : zone.getSubFields()
                 ) {
                // si la sous-zone est celle recherchée
                if (subField.getCode().equals(sousZone)) {
                    // détermination du type de recherche
                    switch (enumTypeDeVerification) {
                        // Si la sous-zone contient STRICTEMENT la/les chaine.s de caractères
                        case STRICTEMENT:
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si il n'y a pas d'opérateur
                                    if (chaineCaracteres.getBooleanOperateur() == null) {
                                        isOk = subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            break;
                        // Si la sous-zone COMMENCE par la/les chaine.s de caractères
                        case COMMENCE:
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si il n'y a pas d'opérateur
                                    if (chaineCaracteres.getBooleanOperateur() == null) {
                                        isOk = subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            break;
                        // Si la sous-zone TERMINE par la/les chaine.s de caractères
                        case TERMINE:
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si il n'y a pas d'opérateur
                                    if (chaineCaracteres.getBooleanOperateur() == null) {
                                        isOk = subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            break;
                        // Si la sous-zone CONTIENT la/les chaine.s de caractères
                        case CONTIENT:
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si il n'y a pas d'opérateur
                                    if (chaineCaracteres.getBooleanOperateur() == null) {
                                        isOk = subField.getValue().contains(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().contains(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().contains(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        return isOk;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet PresenceChaineCaracteres instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
