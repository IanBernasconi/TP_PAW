package ar.edu.itba.paw.models.offering;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import org.hibernate.annotations.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "offerings")
public class Offering {

    private static final Logger LOGGER = LoggerFactory.getLogger(Offering.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offerings_id_seq")
    @SequenceGenerator(sequenceName = "offerings_id_seq", name = "offerings_id_seq", allocationSize = 1)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferingCategory offeringCategory;
    @Column(name = "min_price", nullable = false)
    private float minPrice;
    @Column(name = "max_price", nullable = false)
    private float maxPrice;
    @Column(name = "price_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceType priceType;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    @Column(name = "description", length = 4095)
    private String description;
    @Column(name = "max_guests", nullable = false)
    private int maxGuests;

    @Column(name = "district", nullable = false)
    @Enumerated(EnumType.STRING)
    private District district;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "offering_images", joinColumns = @JoinColumn(name = "offering_id"))
    @Column(name = "image_id")
    private List<Long> imageIds;

    @OneToMany(mappedBy = "offering")
    private List<EventOfferingRelation> relations;

    @Transient
    private OfferingWithEventList mappedRelationInfo;

    @Formula("(SELECT COUNT(*) FROM liked_offerings lo WHERE lo.offering_id = id)")
    private long likes;
    @Formula("(SELECT COALESCE(AVG(r.rating),0) FROM reviews r JOIN events_offerings eo ON eo.relation_id = r.relation_id WHERE eo.offering_id = id)")
    private double averageRating;


    protected Offering(){}

    public Offering(String name, User user, OfferingCategory offeringCategory, String description, float minPrice, float maxPrice, PriceType priceType, int maxGuests, District district) {
        this.name = name;
        this.owner = user;
        this.offeringCategory = offeringCategory;
        this.description = description;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.priceType = priceType;
        this.maxGuests = maxGuests;
        this.district = district;
    }

    public List<EventOfferingRelation> getRelations() {
        return relations;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OfferingCategory getOfferingCategory() {
        return offeringCategory;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public float getMaxPrice() {
        return maxPrice;
    }


    public String toString() {
        return "Offering{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", offeringCategory=" + offeringCategory +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", priceType=" + priceType +
                '}';
    }

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public District getDistrict() {
        return district;
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public boolean getHasImage() {
        return imageIds != null && !imageIds.isEmpty();
    }

    public int getImagesCount() {
        return imageIds != null ? imageIds.size() : 0;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getLikes() {
        return likes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfferingCategory(OfferingCategory offeringCategory) {
        this.offeringCategory = offeringCategory;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public OfferingWithEventList getMappedRelationInfo() {
        if (mappedRelationInfo == null) {
            mappedRelationInfo = new OfferingWithEventList(relations);
        }
        return mappedRelationInfo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offering)) return false;

        Offering offering = (Offering) o;

        return id == offering.id;
    }


}
