package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_id_seq")
    @SequenceGenerator(sequenceName = "images_id_seq", name = "images_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "image")
    private byte[] image;

    protected Image() {}

    public Image(byte[] image) {
        this.image = image;
    }

    public Image(long id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

}
