package net.ddns.encante.telegram.hr.menu.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "menu_data")
public class MenuData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    private String invoker;
    private Long userId;
    private String type;
}
