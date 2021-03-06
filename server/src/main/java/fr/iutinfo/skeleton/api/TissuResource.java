package fr.iutinfo.skeleton.api;

import static fr.iutinfo.skeleton.api.BDDFactory.getDbi;
import static fr.iutinfo.skeleton.api.BDDFactory.tableExist;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import fr.iutinfo.skeleton.common.dto.TissuDto;

@Path("/tissu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TissuResource {
    private static TissuDAO dao = getDbi().open(TissuDAO.class);

    public TissuResource() throws SQLException {
        if (!tableExist("tissu")) {
            dao.createUserTable();
            dao.insert(new Tissu("Tissu1",10,10,"classique","vif",25,15,3));
            dao.insert(new Tissu("Tissu2",10,10,"temporaire","eteint",70,60,2));
            dao.insert(new Tissu("Tissu3",10,10,"classique","eteint",25,15,3));
            dao.insert(new Tissu("Tissu4",10,10,"temporaire","vif",40,30,2));
            dao.insert(new Tissu("Tissu5",10,10,"classique","vif",25,15,1));
            dao.insert(new Tissu("Tissu6",10,10,"classique","eteint",70,60,2));
            dao.insert(new Tissu("Tissu7",10,10,"temporaire","vif",25,15,3));
            dao.insert(new Tissu("Tissu8",10,10,"temporaire","eteint",25,15,3));
            dao.insert(new Tissu("Tissu9",10,10,"classique","vif",25,15,1));
        }
    }
    
    @GET
    public List<TissuDto> getAllTissus() {
        List<Tissu> tissu;
        tissu = dao.affiche();

        return tissu.stream().map(Tissu::convertToDto).collect(Collectors.toList());
    }
    
    @GET
    @Path("/{nom}")
    public TissuDto getOneTissu(@PathParam("nom") String nom) {
        Tissu tissu = dao.findByName(nom);
        if (tissu == null) {
            throw new WebApplicationException(404);
        }
        return tissu.convertToDto();
    }
    
    @POST
    public TissuDto AddTissu(TissuDto dto){
        Tissu tissu = new Tissu();
        tissu.initFromDto(dto);
        dao.insert(tissu);
        return dto;
    }
    
    @DELETE
    @Path("/{nom}")
    public void deleteTissu(@PathParam("nom") String nom) {
        dao.delete(nom);
    }

}
