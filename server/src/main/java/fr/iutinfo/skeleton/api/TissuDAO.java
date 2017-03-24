package fr.iutinfo.skeleton.api;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

public interface TissuDAO {
	
    @SqlUpdate("create table tissu (nom varchar(100) primary key, longueur double,largeur double,type varchar(100),luminosite varchar(100))")
    void createUserTable();
    
    @SqlUpdate("insert into tissu (nom,longueur,largeur,type,luminosite) values (:nom, :longueur , :largeur, :type, :luminosite)")
    @GetGeneratedKeys
    int insert(@BindBean() Tissu tissu);
    
    @SqlQuery("select * from tissu")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Tissu> affiche();
    
    void close();

}