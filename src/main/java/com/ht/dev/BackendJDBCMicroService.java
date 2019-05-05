package com.ht.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class BackendJDBCMicroService implements CommandLineRunner{

     private static final Logger log = LoggerFactory.getLogger(BackendJDBCMicroService.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    /************************************************
     * Insert person en mode GET 
     * @param nom
     * @param prenom
     * @return
     ************************************************/
    @RequestMapping("uphc/person/new/nom/{NOM}/prenom/{PRENOM}")
    @ResponseBody
    public String Insert_Person(@PathVariable("NOM") String nom, @PathVariable("PRENOM") String prenom){
         jdbcTemplate.update("INSERT INTO APP.persons(id,nom, prenom) VALUES (DEFAULT,?,?)", nom, prenom);
         return "Insertion "+nom+" "+prenom+" ok";
    }

    
    /************************************************
     * supprimer person en mode DELETE par son ID 
     * @param nom
     * @param prenom
     * @return
     ************************************************/
	@RequestMapping(value="uphc/person/delete/{ID}",method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> Delete_Person(@PathVariable("ID") String Id){
    	try {
         jdbcTemplate.update("DELETE FROM APP.persons WHERE Id="+ Id);
    	}
    	catch(DataAccessException dae)
    	{
    		return new ResponseEntity<String>("Not possible",HttpStatus.NOT_MODIFIED);
    	}
         return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    
	/************************************************
     * maj person en mode DELETE par son ID 
     * @param nom
     * @param prenom
     * @return
     ************************************************/
	@RequestMapping(value="uphc/person/delete/{ID}/{NOM}/{PRENOM}",method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> Update_Person(@PathVariable("ID") String Id,@PathVariable("NOM") String nom,@PathVariable("PRENOM") String prenom){
    	try {
         jdbcTemplate.update("UPDATE APP.persons set nom='"+nom+"', prenom='"+prenom+"' WHERE Id="+ Id);
    	}
    	catch(DataAccessException dae)
    	{
    		return new ResponseEntity<String>("Not possible",HttpStatus.NOT_MODIFIED);
    	}
         return new ResponseEntity<String>("OK",HttpStatus.OK);
    }
	
    
    /************************************************
     * Insert person en mode POST 
     * @param nom
     * @param prenom
     * @return
     ************************************************/
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/uphc/person/new",method = RequestMethod.POST)
    private ResponseEntity insertPerson(@RequestBody Person person)
    {
    	String nom=person.getNom();
    	String prenom=person.getPrenom();
 
    try
    {
    	jdbcTemplate.update("INSERT INTO APP.persons(nom, prenom) VALUES (?,?)", nom, prenom);
    } catch(DataAccessException dae){return new ResponseEntity("Not possible",HttpStatus.NOT_MODIFIED);}
    
    return new ResponseEntity("OK",HttpStatus.OK);
    }
    
    
    /************************************************
     * lister un utilisateur par son NOM
     * @param nom
     * @param prenom
     * @return
     ************************************************/
    @RequestMapping(value="/uphc/person/get/{nom}",method = RequestMethod.GET)
    private ResponseEntity<List<Person>> getByNameFirstName(@PathVariable("nom") String nom)
    {
    List<Person> persons=new ArrayList<>();
    String sql="select Id, nom, prenom from APP.persons WHERE nom like '%" + nom + "%'";
    List<Map<String,Object>> rows =jdbcTemplate.queryForList(sql);
    rows.forEach((row) -> {
    Person p=new Person();
    p.setId(row.get("ID").toString());
    p.setNom(row.get("NOM").toString());
    p.setPrenom(row.get("PRENOM").toString());
    persons.add(p);
    });
    
    //si la liste est vide
    if (persons.isEmpty()) {return new ResponseEntity<List<Person>>(HttpStatus.NOT_FOUND);}
    //sinon on retourne la liste
    return new ResponseEntity<>(persons, HttpStatus.OK);
    }
    
    
    /************************************************
     * lister un utilisateur par son ID
     * @param nom
     * @param prenom
     * @return
     ************************************************/
    @RequestMapping(value="/uphc/person/get/{ID}",method = RequestMethod.GET)
    private ResponseEntity<List<Person>> getById(@PathVariable("ID") String Id)
    {
    List<Person> persons=new ArrayList<>();
    String sql="select Id, nom, prenom from APP.persons WHERE ID="+Id+"";
    List<Map<String,Object>> rows =jdbcTemplate.queryForList(sql);
    rows.forEach((row) -> {
    Person p=new Person();
    p.setId(row.get("ID").toString());
    p.setNom(row.get("NOM").toString());
    p.setPrenom(row.get("PRENOM").toString());
    persons.add(p);
    });
    
    //si la liste est vide
    if (persons.isEmpty()) {return new ResponseEntity<List<Person>>(HttpStatus.NOT_FOUND);}
    //sinon on retourne la liste
    return new ResponseEntity<>(persons, HttpStatus.OK);
    }
    
    
    /******************************************************
     * Recuperer tous les utilisateurs
     * @return
     ******************************************************/
    @RequestMapping(value="/uphc/person/getall",method = RequestMethod.GET)
    private ResponseEntity<List<Person>> getAll()
    {
    List<Person> persons=new ArrayList<>();
    String sql="select id,nom, prenom from APP.persons";
    List<Map<String,Object>> rows =jdbcTemplate.queryForList(sql);
    rows.forEach((row) -> {
    Person p=new Person();
    p.setId(row.get("ID").toString());
    p.setNom(row.get("NOM").toString());
    p.setPrenom(row.get("PRENOM").toString());
    persons.add(p);
    });
    
    //si la liste est vide
    if (persons.isEmpty()) {return new ResponseEntity<List<Person>>(HttpStatus.NOT_FOUND);}
    //sinon on retourne la liste
    return new ResponseEntity<>(persons, HttpStatus.OK);
    }
    
    
    
    /**********************************************
     * Creer la table a l'ouverture du service 
     * si elle n'existe pas...
     **********************************************/
    @Override
    public void run(String... strings) {

        log.info("Creation de la table");

        try {
        //jdbcTemplate.execute("DROP TABLE persons");
        jdbcTemplate.execute("CREATE TABLE APP.persons(id INTEGER generated always as identity, prenom VARCHAR(255), nom VARCHAR(255))");
        }
        catch(DataAccessException e) 
        {
        //jdbcTemplate.execute("CREATE TABLE APP.persons(id INTEGER generated always as identity, prenom VARCHAR(255), nom VARCHAR(255))");
        }
        }

    /***********************
     * MAIN ENTRY POINT
     * @param args
     ***********************/
	public static void main(String[] args) {
		SpringApplication.run(BackendJDBCMicroService.class, args);
	}
}
