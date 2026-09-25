package org.sagebionetworks.model.ad.api.next.model.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Represents a composite identifier for transcriptomics documents.
 * Format: ensembl_gene_id~name~sex (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female")
 *
 * <p>This is also the parent identity of a proteomics row, since several protein isoforms roll up
 * to one gene. {@link #FIELDS} is what lets a repository declare that parent item filter.
 */
@Value
@Builder
@Getter
public class TranscriptomicsIdentifier {

  // MG-586 - Consider using existing CompositeIdentifier utility if applicable
  String ensemblGeneId;
  String name;
  String sex;

  private static final String DELIMITER = "~";

  /**
   * Rendered in place of a blank part (null, empty, or whitespace only) by
   * {@link #toCompositeId()}. {@link #parse(String)} reads it back as a null part, which
   * {@link #toCriteria()} matches against a blank field, so a row with a blank part stays
   * addressable by its own token.
   *
   * <p>Must agree with the fallback the aggregation pipeline emits when it builds this same token
   * from a document with a blank field ({@code ItemFilterDef.MISSING_PART}), or a parent-scoped
   * fetch would look for a token no row can produce. {@code TranscriptomicsIdentifierTest} is where
   * the two are checked against each other.
   */
  private static final String MISSING_PART = "null";

  /**
   * The token's parts in order, each paired with the MongoDB path it matches. This is the token
   * format's one declaration: {@link #FIELDS}, {@link #toCompositeId()}, {@link #toCriteria()}, and
   * the part count {@link #parse(String)} expects all derive from it.
   */
  private static final List<CompositeField> COMPOSITE_FIELDS = List.of(
    new CompositeField("ensembl_gene_id", TranscriptomicsIdentifier::getEnsemblGeneId),
    new CompositeField("name.link_text", TranscriptomicsIdentifier::getName),
    new CompositeField("sex", TranscriptomicsIdentifier::getSex)
  );

  /**
   * The MongoDB paths this token's parts match, in token order — what a repository hands to
   * {@code ItemFilterDef.Composite} so the pipeline can rebuild the token from a document.
   */
  public static final List<String> FIELDS = COMPOSITE_FIELDS.stream()
    .map(CompositeField::path)
    .toList();

  /**
   * One part of the token.
   *
   * @param path the MongoDB path the part matches
   * @param value reads the part's value off an identifier
   */
  private record CompositeField(String path, Function<TranscriptomicsIdentifier, String> value) {}

  /**
   * Parses a composite identifier string into a TranscriptomicsIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static TranscriptomicsIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, -1); // -1 to include trailing empty strings

    if (parts.length != COMPOSITE_FIELDS.size()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. Expected format: 'ensembl_gene_id~name~sex' (e.g., 'ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female')",
          compositeId
        )
      );
    }

    String ensemblGeneId = parts[0].trim();
    String name = parts[1].trim();
    String sex = parts[2].trim();

    if (ensemblGeneId.isEmpty() || name.isEmpty() || sex.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (ensembl_gene_id, name, sex) must be non-empty",
          compositeId
        )
      );
    }

    return TranscriptomicsIdentifier.builder()
      .ensemblGeneId(parsePart(ensemblGeneId))
      .name(parsePart(name))
      .sex(parsePart(sex))
      .build();
  }

  private static String parsePart(String part) {
    return MISSING_PART.equals(part) ? null : part;
  }

  /**
   * Returns the composite identifier as a string. A blank part renders as {@link #MISSING_PART}.
   *
   * @return the composite identifier string
   */
  public String toCompositeId() {
    return COMPOSITE_FIELDS.stream()
      .map(field -> renderPart(field.value().apply(this)))
      .collect(Collectors.joining(DELIMITER));
  }

  private static String renderPart(String value) {
    return value == null || value.isBlank() ? MISSING_PART : value;
  }

  /**
   * Converts this identifier to a MongoDB {@link Criteria}
   * that matches documents with this exact ensembl_gene_id, name.link_text, and sex. A null part
   * matches a blank field -- null, missing, empty, or whitespace only -- the same set of values
   * {@link #toCompositeId()} renders as {@link #MISSING_PART}.
   *
   * @return a Criteria requiring all fields to match
   */
  public Criteria toCriteria() {
    return new Criteria()
      .andOperator(
        COMPOSITE_FIELDS.stream()
          .map(field -> partCriteria(field.path(), field.value().apply(this)))
          .toList()
      );
  }

  private static Criteria partCriteria(String path, String value) {
    return value != null ? Criteria.where(path).is(value) : ApiHelper.blankFieldCriteria(path);
  }
}
