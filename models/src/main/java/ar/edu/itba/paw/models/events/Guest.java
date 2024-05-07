package ar.edu.itba.paw.models.events;

import javax.persistence.*;

@Entity
@Table(name = "event_guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_guests_id_seq")
    @SequenceGenerator(sequenceName = "event_guests_id_seq", name = "event_guests_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "guest_mail", length = 255, nullable = false)
    private String email;

    @Column(name = "invite_status", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private GuestStatus status;

    @Column(name = "invite_token", length = 255)
    private String token;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Guest() {
    }

    public Guest(String email, Event event, GuestStatus status) {
        this.email = email;
        this.event = event;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
