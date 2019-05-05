package com.ht.dev;

public class Person {
    
    public String getId() {
		return id;
	}

	public void setId(String string) {
		this.id = string;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String firstName) {
		this.prenom = firstName;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String lastName) {
		this.nom = lastName;
	}

	private String id;
    private String prenom, nom;

    public Person(String firstName, String lastName) {
        //this.id = id;
        this.prenom = firstName;
        this.nom = lastName;
    }

    public Person() {
    }
   
}

