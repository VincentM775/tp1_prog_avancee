package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnnonceSpecificationsTest {

    private Root<Annonce> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;
    private Path<Object> path;
    private Path<Object> nestedPath;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
        path = mock(Path.class);
        nestedPath = mock(Path.class);

        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(nestedPath);
    }

    @Test
    void keywordInTitleOrDescription_shouldCreateLikePredicate() {
        Expression<String> expr = mock(Expression.class);
        when(cb.lower(any())).thenReturn(expr);

        Predicate predicate = mock(Predicate.class);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.keywordInTitleOrDescription("test");
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void hasStatus_shouldCreateEqualPredicate() {
        Predicate predicate = mock(Predicate.class);
        when(cb.equal(any(), eq(AnnonceStatus.PUBLISHED))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.hasStatus(AnnonceStatus.PUBLISHED);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("status");
    }

    @Test
    void hasCategoryId_shouldCreateEqualPredicate() {
        Predicate predicate = mock(Predicate.class);
        when(cb.equal(any(), eq(1L))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.hasCategoryId(1L);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("category");
    }

    @Test
    void hasAuthorId_shouldCreateEqualPredicate() {
        Predicate predicate = mock(Predicate.class);
        when(cb.equal(any(), eq(1L))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(1L);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("author");
    }

    @Test
    void createdAfter_shouldCreateGreaterThanOrEqualPredicate() {
        Predicate predicate = mock(Predicate.class);
        Timestamp ts = Timestamp.valueOf("2025-01-01 00:00:00");
        when(cb.greaterThanOrEqualTo(any(), eq(ts))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.createdAfter(ts);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("date");
    }

    @Test
    void createdBefore_shouldCreateLessThanOrEqualPredicate() {
        Predicate predicate = mock(Predicate.class);
        Timestamp ts = Timestamp.valueOf("2025-12-31 23:59:59");
        when(cb.lessThanOrEqualTo(any(), eq(ts))).thenReturn(predicate);

        Specification<Annonce> spec = AnnonceSpecifications.createdBefore(ts);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("date");
    }
}
