package eu.execom.fabut.model;

public class TypeWithAllWaysOfGettingFields extends TierOneType {

	private String fieldWithGetters;
	private Integer fieldWithScalaLikeGetters;
	public Long fieldPublic;
	protected Long fieldProtected = 2l;
	private final Long fieldPrivate = 3l;

	private static final String PRIVATE_CONSTANT = "private constant";
	public static final String PUBLIC_CONSTANT = "public constant";

	public String getFieldWithGetters() {
		return fieldWithGetters;
	}

	public void setFieldWithGetters(final String fieldWithGetters) {
		this.fieldWithGetters = fieldWithGetters;
	}

	public void fieldWithScalaLikeGetters(final Integer fieldWithScalaLikeGetters) {
		this.fieldWithScalaLikeGetters = fieldWithScalaLikeGetters;
	}

	public Integer fieldWithScalaLikeGetters() {
		return fieldWithScalaLikeGetters;
	}
}
