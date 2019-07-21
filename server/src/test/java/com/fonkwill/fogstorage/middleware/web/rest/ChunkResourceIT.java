package com.fonkwill.fogstorage.middleware.web.rest;

import com.fonkwill.fogstorage.middleware.MiddlewareApp;
import com.fonkwill.fogstorage.middleware.domain.Chunk;
import com.fonkwill.fogstorage.middleware.repository.ChunkRepository;
import com.fonkwill.fogstorage.middleware.service.ChunkService;
import com.fonkwill.fogstorage.middleware.web.rest.errors.ExceptionTranslator;

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

import static com.fonkwill.fogstorage.middleware.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ChunkResource} REST controller.
 */
@SpringBootTest(classes = MiddlewareApp.class)
public class ChunkResourceIT {

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final Long DEFAULT_SIZE = 1L;
    private static final Long UPDATED_SIZE = 2L;

    private static final Integer DEFAULT_ACCESSED = 1;
    private static final Integer UPDATED_ACCESSED = 2;

    private static final Boolean DEFAULT_STORED_LOCALLY = false;
    private static final Boolean UPDATED_STORED_LOCALLY = true;

    private static final String DEFAULT_PARTNER = "AAAAAAAAAA";
    private static final String UPDATED_PARTNER = "BBBBBBBBBB";

    @Autowired
    private ChunkRepository chunkRepository;

    @Autowired
    private ChunkService chunkService;

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

    private MockMvc restChunkMockMvc;

    private Chunk chunk;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChunkResource chunkResource = new ChunkResource(chunkService);
        this.restChunkMockMvc = MockMvcBuilders.standaloneSetup(chunkResource)
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
    public static Chunk createEntity(EntityManager em) {
        Chunk chunk = new Chunk()
            .uuid(DEFAULT_UUID)
            .size(DEFAULT_SIZE)
            .accessed(DEFAULT_ACCESSED)
            .storedLocally(DEFAULT_STORED_LOCALLY)
            .partner(DEFAULT_PARTNER);
        return chunk;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chunk createUpdatedEntity(EntityManager em) {
        Chunk chunk = new Chunk()
            .uuid(UPDATED_UUID)
            .size(UPDATED_SIZE)
            .accessed(UPDATED_ACCESSED)
            .storedLocally(UPDATED_STORED_LOCALLY)
            .partner(UPDATED_PARTNER);
        return chunk;
    }

    @BeforeEach
    public void initTest() {
        chunk = createEntity(em);
    }

    @Test
    @Transactional
    public void createChunk() throws Exception {
        int databaseSizeBeforeCreate = chunkRepository.findAll().size();

        // Create the Chunk
        restChunkMockMvc.perform(post("/api/chunks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chunk)))
            .andExpect(status().isCreated());

        // Validate the Chunk in the database
        List<Chunk> chunkList = chunkRepository.findAll();
        assertThat(chunkList).hasSize(databaseSizeBeforeCreate + 1);
        Chunk testChunk = chunkList.get(chunkList.size() - 1);
        assertThat(testChunk.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testChunk.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThat(testChunk.getAccessed()).isEqualTo(DEFAULT_ACCESSED);
        assertThat(testChunk.isStoredLocally()).isEqualTo(DEFAULT_STORED_LOCALLY);
        assertThat(testChunk.getPartner()).isEqualTo(DEFAULT_PARTNER);
    }

    @Test
    @Transactional
    public void createChunkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chunkRepository.findAll().size();

        // Create the Chunk with an existing ID
        chunk.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChunkMockMvc.perform(post("/api/chunks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chunk)))
            .andExpect(status().isBadRequest());

        // Validate the Chunk in the database
        List<Chunk> chunkList = chunkRepository.findAll();
        assertThat(chunkList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllChunks() throws Exception {
        // Initialize the database
        chunkRepository.saveAndFlush(chunk);

        // Get all the chunkList
        restChunkMockMvc.perform(get("/api/chunks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chunk.getId().intValue())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].accessed").value(hasItem(DEFAULT_ACCESSED)))
            .andExpect(jsonPath("$.[*].storedLocally").value(hasItem(DEFAULT_STORED_LOCALLY.booleanValue())))
            .andExpect(jsonPath("$.[*].partner").value(hasItem(DEFAULT_PARTNER.toString())));
    }
    
    @Test
    @Transactional
    public void getChunk() throws Exception {
        // Initialize the database
        chunkRepository.saveAndFlush(chunk);

        // Get the chunk
        restChunkMockMvc.perform(get("/api/chunks/{id}", chunk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chunk.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.intValue()))
            .andExpect(jsonPath("$.accessed").value(DEFAULT_ACCESSED))
            .andExpect(jsonPath("$.storedLocally").value(DEFAULT_STORED_LOCALLY.booleanValue()))
            .andExpect(jsonPath("$.partner").value(DEFAULT_PARTNER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingChunk() throws Exception {
        // Get the chunk
        restChunkMockMvc.perform(get("/api/chunks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChunk() throws Exception {
        // Initialize the database
        chunkService.save(chunk);

        int databaseSizeBeforeUpdate = chunkRepository.findAll().size();

        // Update the chunk
        Chunk updatedChunk = chunkRepository.findById(chunk.getId()).get();
        // Disconnect from session so that the updates on updatedChunk are not directly saved in db
        em.detach(updatedChunk);
        updatedChunk
            .uuid(UPDATED_UUID)
            .size(UPDATED_SIZE)
            .accessed(UPDATED_ACCESSED)
            .storedLocally(UPDATED_STORED_LOCALLY)
            .partner(UPDATED_PARTNER);

        restChunkMockMvc.perform(put("/api/chunks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChunk)))
            .andExpect(status().isOk());

        // Validate the Chunk in the database
        List<Chunk> chunkList = chunkRepository.findAll();
        assertThat(chunkList).hasSize(databaseSizeBeforeUpdate);
        Chunk testChunk = chunkList.get(chunkList.size() - 1);
        assertThat(testChunk.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testChunk.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testChunk.getAccessed()).isEqualTo(UPDATED_ACCESSED);
        assertThat(testChunk.isStoredLocally()).isEqualTo(UPDATED_STORED_LOCALLY);
        assertThat(testChunk.getPartner()).isEqualTo(UPDATED_PARTNER);
    }

    @Test
    @Transactional
    public void updateNonExistingChunk() throws Exception {
        int databaseSizeBeforeUpdate = chunkRepository.findAll().size();

        // Create the Chunk

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChunkMockMvc.perform(put("/api/chunks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chunk)))
            .andExpect(status().isBadRequest());

        // Validate the Chunk in the database
        List<Chunk> chunkList = chunkRepository.findAll();
        assertThat(chunkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteChunk() throws Exception {
        // Initialize the database
        chunkService.save(chunk);

        int databaseSizeBeforeDelete = chunkRepository.findAll().size();

        // Delete the chunk
        restChunkMockMvc.perform(delete("/api/chunks/{id}", chunk.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Chunk> chunkList = chunkRepository.findAll();
        assertThat(chunkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chunk.class);
        Chunk chunk1 = new Chunk();
        chunk1.setId(1L);
        Chunk chunk2 = new Chunk();
        chunk2.setId(chunk1.getId());
        assertThat(chunk1).isEqualTo(chunk2);
        chunk2.setId(2L);
        assertThat(chunk1).isNotEqualTo(chunk2);
        chunk1.setId(null);
        assertThat(chunk1).isNotEqualTo(chunk2);
    }
}
