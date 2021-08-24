package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    ClinicService service;
    @Mock
    Owner owner;

    @InjectMocks
    OwnerController controller;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    public void findByNameNotFound() throws Exception {
        mockMvc.perform(get("/owners")
                    .param("lastName", "Dont find Me"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testReturnListOfOwners() throws Exception {
        given(service.findOwnerByLastName("")).willReturn(Lists.newArrayList(owner, owner));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"))
                .andExpect(model().attributeExists("selections"));

        then(service).should().findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
    }

    @Test
    void testFindOwnerOneResult() throws Exception {
        given(service.findOwnerByLastName("FindJustOne")).willReturn(Lists.newArrayList(owner));

        mockMvc.perform(get("/owners")
                        .param("lastName", "FindJustOne"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + owner.getId()));

        then(service).should().findOwnerByLastName(anyString());
    }

    @Test
    public void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St")
                        .param("city", "Key West")
                        .param("telephone", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + null));
    }

    @Test
    public void testNewOwnerPostNotValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                        .param("id", "1")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St")
                        .param("city", "Key West"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/createOrUpdateOwnerForm"))
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"));
    }

    @Test
    public void testUpdateOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                        .param("id", "1")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St")
                        .param("city", "Key West")
                        .param("telephone", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    public void testUpdateOwnerPostInValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                        .param("id", "1")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St")
                        .param("city", "Key West"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
}