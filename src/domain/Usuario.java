package domain;

import java.time.Instant;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String name;
    private String document; // pode ser CNPJ no caso de empresas
    private String email;
    private String password;
    private String url_foto_perfil;

    public Usuario(UUID id, String name, String document, String email, String password) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl_foto_perfil() {
        return url_foto_perfil;
    }

    public void setUrl_foto_perfil(String url_foto_perfil) {
        this.url_foto_perfil = url_foto_perfil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public UUID getId() {
        return id;
    }
}
