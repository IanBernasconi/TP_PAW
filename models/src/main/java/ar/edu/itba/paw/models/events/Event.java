package ar.edu.itba.paw.models.events;

import ar.edu.itba.paw.models.Conversation;
import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "events")
public class Event {

    @Transient
    private final List<OfferingCategory> defaultCategories = Arrays.asList(OfferingCategory.PHOTOGRAPHY, OfferingCategory.MUSIC, OfferingCategory.CATERING, OfferingCategory.VENUE);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_id_seq")
    @SequenceGenerator(sequenceName = "events_id_seq", name = "events_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name",  length = 1023)
    private String name;

    @Column(name = "description", length = 4095)
    private String description;

    @Column(name = "date")
    private Date date;

    @Column(name = "number_of_guests")
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "district", nullable = false)
    private District district;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventOfferingRelation> eventOfferingRelations;

    @Transient
    private Map<OfferingCategory,List<EventOfferingRelation>> relationsByOfferingCategory;

    @Transient
    private boolean isFinished;

    @PostLoad
    private void postLoad(){
        isFinished = date.before(new Date());
    }

    protected Event() {}

    public Event(User user, String name, String description, Date date, int numberOfGuests, District district) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.date = date;
        this.numberOfGuests = numberOfGuests;
        this.district = district;
    }

//    public Event(User user, String name, String description, Date date, int numberOfGuests) {
//        this.user = user;
//        this.name = name;
//        this.description = description;
//        this.date = date;
//        this.numberOfGuests = numberOfGuests;
//        this.location = "undefined";
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public List<EventOfferingRelation> getEventOfferingRelations() {
        return eventOfferingRelations;
    }

    public boolean isFinished() {
        return isFinished;
    }

    private void populateOfferingByCategory() {
        relationsByOfferingCategory = new LinkedHashMap<>();
        for (EventOfferingRelation relation : eventOfferingRelations) {
            Offering offering = relation.getOffering();
            OfferingCategory category = offering.getOfferingCategory();
            relationsByOfferingCategory.putIfAbsent(category, new ArrayList<>());
            relationsByOfferingCategory.get(category).add(relation);
        }
    }

    public List<Offering> getOfferings(){
        List<Offering> offerings = new ArrayList<>();
        for(EventOfferingRelation relation : eventOfferingRelations){
            offerings.add(relation.getOffering());
        }
        return offerings;
    }

    public Optional<EventOfferingRelation> getOfferingWithRelationId(long relationId){
        return eventOfferingRelations.stream().filter(relation -> relation.getRelationId() == relationId).findFirst();
    }

    public Map<OfferingCategory, List<EventOfferingRelation>> getRelationsByOfferingCategory() {
        if (relationsByOfferingCategory == null) {
            populateOfferingByCategory();
        }
        return relationsByOfferingCategory;
    }

    public Map<OfferingCategory, List<EventOfferingRelation>> getRelationsByOfferingCategoryWithDefaults() {
        Map<OfferingCategory, List<EventOfferingRelation>> relations = getRelationsByOfferingCategory();
        for (OfferingCategory category : defaultCategories) {
            relations.putIfAbsent(category, new ArrayList<>());
        }
        return relations;
    }


    public int getOrganizerUnreadMessagesCount() {
        int rlt =0;
        for(EventOfferingRelation relation : eventOfferingRelations){
            if(relation.getStatus() != OfferingStatus.NEW) {
                Conversation conversation = relation.getConversation();
                if (conversation != null) {
                    rlt += conversation.getOrganizerUnreadMessagesCount();
                }
            }
        }

        return rlt;
    }

    public List<Offering> getNotNewOfferings(){
        return eventOfferingRelations.stream().filter(relation -> relation.getStatus() != OfferingStatus.NEW).map(EventOfferingRelation::getOffering).collect(Collectors.toList());
    }

    public List<EventOfferingRelation> getNotNewOfferingRelations(){
        return eventOfferingRelations.stream().filter(relation -> relation.getStatus() != OfferingStatus.NEW).collect(Collectors.toList());
    }

}
