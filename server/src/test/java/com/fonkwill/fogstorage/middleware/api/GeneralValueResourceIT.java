package com.fonkwill.fogstorage.middleware.api;

import com.fonkwill.fogstorage.middleware.MiddlewareApp;
import com.fonkwill.fogstorage.middleware.api.GeneralValueResource;
import com.fonkwill.fogstorage.middleware.shared.domain.GeneralValue;
import com.fonkwill.fogstorage.middleware.shared.repository.GeneralValueRepository;
import com.fonkwill.fogstorage.middleware.shared.service.GeneralValueService;
import com.fonkwill.fogstorage.middleware.api.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.fonkwill.fogstorage.middleware.api.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link GeneralValueResource} REST controller.
 */
@SpringBootTest(classes = MiddlewareApp.class)
public class GeneralValueResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    @Autowired
    private GeneralValueRepository generalValueRepository;

    @Autowired
    private GeneralValueService generalValueService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restGeneralValueMockMvc;

    private GeneralValue generalValue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GeneralValueResource generalValueResource = new GeneralValueResource(generalValueService);
        this.restGeneralValueMockMvc = MockMvcBuilders.standaloneSetup(generalValueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GeneralValue createEntity(EntityManager em) {
        GeneralValue generalValue = new GeneralValue()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE)
            .type(DEFAULT_TYPE);
        return generalValue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GeneralValue createUpdatedEntity(EntityManager em) {
        GeneralValue generalValue = new GeneralValue()
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .type(UPDATED_TYPE);
        return generalValue;
    }

    @BeforeEach
    public void initTest() {
        generalValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createGeneralValue() throws Exception {
        int databaseSizeBeforeCreate = generalValueRepository.findAll().size();

        // Create the GeneralValue
        restGeneralValueMockMvc.perform(post("/api/general-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(generalValue)))
            .andExpect(status().isCreated());

        // Validate the GeneralValue in the database
        List<GeneralValue> generalValueList = generalValueRepository.findAll();
        assertThat(generalValueList).hasSize(databaseSizeBeforeCreate + 1);
        GeneralValue testGeneralValue = generalValueList.get(generalValueList.size() - 1);
        assertThat(testGeneralValue.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testGeneralValue.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testGeneralValue.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createGeneralValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = generalValueRepository.findAll().size();

        // Create the GeneralValue with an existing ID
        generalValue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGeneralValueMockMvc.perform(post("/api/general-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(generalValue)))
            .andExpect(status().isBadRequest());

        // Validate the GeneralValue in the database
        List<GeneralValue> generalValueList = generalValueRepository.findAll();
        assertThat(generalValueList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllGeneralValues() throws Exception {
        // Initialize the database
        generalValueRepository.saveAndFlush(generalValue);

        // Get all the generalValueList
        restGeneralValueMockMvc.perform(get("/api/general-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(generalValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getGeneralValue() throws Exception {
        // Initialize the database
        generalValueRepository.saveAndFlush(generalValue);

        // Get the generalValue
        restGeneralValueMockMvc.perform(get("/api/general-values/{id}", generalValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(generalValue.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGeneralValue() throws Exception {
        // Get the generalValue
        restGeneralValueMockMvc.perform(get("/api/general-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGeneralValue() throws Exception {
        // Initialize the database
        generalValueService.save(generalValue);

        int databaseSizeBeforeUpdate = generalValueRepository.findAll().size();

        // Update the generalValue
        GeneralValue updatedGeneralValue = generalValueRepository.findById(generalValue.getId()).get();
        // Disconnect from session so that the updates on updatedGeneralValue are not directly saved in db
        em.detach(updatedGeneralValue);
        updatedGeneralValue
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .type(UPDATED_TYPE);

        restGeneralValueMockMvc.perform(put("/api/general-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGeneralValue)))
            .andExpect(status().isOk());

        // Validate the GeneralValue in the database
        List<GeneralValue> generalValueList = generalValueRepository.findAll();
        assertThat(generalValueList).hasSize(databaseSizeBeforeUpdate);
        GeneralValue testGeneralValue = generalValueList.get(generalValueList.size() - 1);
        assertThat(testGeneralValue.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testGeneralValue.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testGeneralValue.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingGeneralValue() throws Exception {
        int databaseSizeBeforeUpdate = generalValueRepository.findAll().size();

        // Create the GeneralValue

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGeneralValueMockMvc.perform(put("/api/general-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(generalValue)))
            .andExpect(status().isBadRequest());

        // Validate the GeneralValue in the database
        List<GeneralValue> generalValueList = generalValueRepository.findAll();
        assertThat(generalValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGeneralValue() throws Exception {
        // Initialize the database
        generalValueService.save(generalValue);

        int databaseSizeBeforeDelete = generalValueRepository.findAll().size();

        // Delete the generalValue
        restGeneralValueMockMvc.perform(delete("/api/general-values/{id}", generalValue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GeneralValue> generalValueList = generalValueRepository.findAll();
        assertThat(generalValueList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GeneralValue.class);
        GeneralValue generalValue1 = new GeneralValue();
        generalValue1.setId(1L);
        GeneralValue generalValue2 = new GeneralValue();
        generalValue2.setId(generalValue1.getId());
        assertThat(generalValue1).isEqualTo(generalValue2);
        generalValue2.setId(2L);
        assertThat(generalValue1).isNotEqualTo(generalValue2);
        generalValue1.setId(null);
        assertThat(generalValue1).isNotEqualTo(generalValue2);
    }
}
