package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOutSiteVM implements Serializable {
   private  Long id;
   private String codeUserOutSite;
   private String emailOutSite;
   private String idOutSite;
   private String nameOutSite;
   private String phoneOutSite;
   private String positionOutSite;
   private Integer sexOutSite;
   private Integer statusOutSite;
}
