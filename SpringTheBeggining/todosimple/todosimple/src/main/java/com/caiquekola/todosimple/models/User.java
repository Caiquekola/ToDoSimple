package com.caiquekola.todosimple.models;

import com.caiquekola.todosimple.models.enums.ProfileEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.*;

@Entity
@Table(name = User.TABLE_NAME)

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    public static final String TABLE_NAME = "user";
    public interface CreateUser{}
    public interface UpdateUser{}
    //Validações

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true)
    private Long id;


    @Column(name = "username",unique = true,
            nullable = false,length = 50)
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
    @Size(groups = CreateUser.class, min = 5, max = 50)
    private String username;


    //Poderia ser NotBlank
    //Senha só poder escrita, nunca é lida e retornada ao usuário
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "password",
            nullable = false,length = 100)
    @NotNull(groups = {CreateUser.class,UpdateUser.class})
    @NotEmpty(groups = CreateUser.class)
    @Size(groups = CreateUser.class,min = 5, max = 100)
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty(access = Access.WRITE_ONLY)
    @CollectionTable(name = "user_profile")
    @Column(name = "profile", nullable = false)
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles(){
        return this.profiles.stream().map(x -> ProfileEnum.getProfileEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profile){
        this.profiles.add(profile.getCode());
    }
}
