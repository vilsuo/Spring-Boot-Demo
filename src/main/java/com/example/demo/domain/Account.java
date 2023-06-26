
package com.example.demo.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class Account extends AbstractPersistable<Long>{
	
	/*
	a String field constrained with @NotBlank must be not null, and the trimmed 
	length must be greater than zero.
	
	IF YOU CHANGE VALIDATION OPTIONS HERE, CHANGE ALSO IN THE CLASS AccountDto!
	*/
	@NotBlank
	@Size(min = 1, max = 20)
	private String username;
	
	/*
	The @NotEmpty annotation makes use of the @NotNull class' isValid() 
	implementation, and also checks that the size/length of the supplied object 
	is greater than zero.
	
	Length of the actual password is defined in the class AccountDto.
	*/
	@NotEmpty
	private String password;
	
	@NotNull()
	private Role role;
	
	//@ManyToMany(fetch = FetchType.LAZY)
	@ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "relation",
        joinColumns = @JoinColumn(name = "account_follower_id"),
        inverseJoinColumns = @JoinColumn(name = "account_following_id")
	)
    private List<Account> following;
	/*
	@OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private Set<AccountRelation> followers;

	@OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private Set<AccountRelation> following = new HashSet<>();
	*/

	// TODO (implement similiarly as following/followers)
	//@ManyToMany(cascade = CascadeType.ALL)
	//private Set<Account> blockedAccounts = new HashSet<>();
	
	/*
	// TODO
	
	 Profiilikuva
	- Käyttäjä voi määritellä yhden kuva-albumissa olevan kuvan profiilikuvaksi.
	
	- Profiiliteksti
	
	- Seuraajat
		Käyttäjä voi tarkastella omia seuraajiaan. Seurauksen yhteydessä 
		näytetään seuraajan nimi sekä seurauksen aloitusaika. Seuraajan voi myös 
		halutessaan torjua seuraamasta, tällöin seuraus ei näy kummankaan 
		profiilissa.
	List<Account> following;
	
	
	- Kuva-albumi
		Jokaisella käyttäjällä on kuva-albumi. Käyttäjä voi lisätä albumiinsa 
		kuvia ja myös poistaa niitä. Kunkin käyttäjän kuva-albumi voi sisältää 
		korkeintaan 10 kuvaa. Jokaiseen kuvaan liittyy myös tekstimuotoinen 
		kuvaus, joka lisätään kuvaan kuvan lisäyksen yhteydessä.
	List<Picture> pictures;
	
	
	List<Post> posts;
	*/
}
