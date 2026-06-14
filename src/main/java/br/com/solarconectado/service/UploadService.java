package br.com.solarconectado.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import br.com.solarconectado.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile arquivo, String pasta) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Arquivo não pode ser vazio");
        }

        String tipo = arquivo.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            throw new RegraDeNegocioException("Somente imagens são permitidas (JPEG, PNG, WebP, etc.)");
        }

        try {
            Map resultado = cloudinary.uploader().upload(
                    arquivo.getBytes(),
                    ObjectUtils.asMap("folder", "solar-conectado/" + pasta)
            );
            return (String) resultado.get("secure_url");
        } catch (IOException e) {
            throw new RegraDeNegocioException("Falha ao fazer upload da imagem");
        }
    }
}