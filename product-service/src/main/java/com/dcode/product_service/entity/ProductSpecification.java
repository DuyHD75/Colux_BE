    package com.dcode.product_service.entity;

    import org.springframework.data.jpa.domain.Specification;
    import jakarta.persistence.criteria.*;

    import java.util.ArrayList;
    import java.util.List;

    public class ProductSpecification implements Specification<Product> {

        private final List<String> features;
        private final List<String> properties;
        private final Double rating;
        private final Double minPrice;
        private final Double maxPrice;
        private final String type;

        public ProductSpecification(List<String> features, List<String> properties, Double rating, Double minPrice, Double maxPrice, String type) {
            this.features = features;
            this.properties = properties;
            this.rating = rating;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.type = type;
        }

        @Override
        public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by product type (paint, wallpaper, floor)
            if (type != null) {
                if (type.equalsIgnoreCase("paint")) {
                    Join<Product, Paint> paintJoin = root.join("paints", JoinType.LEFT);
                    predicates.add(cb.isNotNull(paintJoin.get("id")));
                    Join<Paint, PaintVariant> paintVariantJoin = paintJoin.join("paintVariants", JoinType.LEFT);
                    if (minPrice != null) {
                        predicates.add(cb.greaterThanOrEqualTo(paintVariantJoin.get("price"), minPrice));
                    }
                    if (maxPrice != null) {
                        predicates.add(cb.lessThanOrEqualTo(paintVariantJoin.get("price"), maxPrice));
                    }
                } else if (type.equalsIgnoreCase("wallpaper")) {
                    Join<Product, Wallpaper> wallpaperJoin = root.join("wallpapers", JoinType.LEFT);
                    predicates.add(cb.isNotNull(wallpaperJoin.get("id")));
                    Join<Wallpaper, WallpaperVariant> wallpaperVariantJoin = wallpaperJoin.join("wallpaperVariants", JoinType.LEFT);
                    if (minPrice != null) {
                        predicates.add(cb.greaterThanOrEqualTo(wallpaperVariantJoin.get("price"), minPrice));
                    }
                    if (maxPrice != null) {
                        predicates.add(cb.lessThanOrEqualTo(wallpaperVariantJoin.get("price"), maxPrice));
                    }
                } else if (type.equalsIgnoreCase("floor")) {
                    Join<Product, Floor> floorJoin = root.join("floors", JoinType.LEFT);
                    predicates.add(cb.isNotNull(floorJoin.get("id")));
                    Join<Floor, FloorVariant> floorVariantJoin = floorJoin.join("floorVariants", JoinType.LEFT);
                    if (minPrice != null) {
                        predicates.add(cb.greaterThanOrEqualTo(floorVariantJoin.get("price"), minPrice));
                    }
                    if (maxPrice != null) {
                        predicates.add(cb.lessThanOrEqualTo(floorVariantJoin.get("price"), maxPrice));
                    }
                }
            }

            // Subquery for ensuring the product has all features
            if (features != null && !features.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Product> subRoot = subquery.from(Product.class);
                Join<Product, FeatureValue> subFeatureValueJoin = subRoot.join("featureValues", JoinType.LEFT);
                Join<FeatureValue, Feature> subFeatureJoin = subFeatureValueJoin.join("feature", JoinType.LEFT);

                // Subquery filters for matching features
                Predicate subqueryPredicate = cb.equal(root.get("id"), subRoot.get("id"));
                Predicate featuresMatchPredicate = subFeatureJoin.get("featureId").in(features);

                subquery.select(cb.countDistinct(subFeatureJoin.get("id")))
                        .where(cb.and(subqueryPredicate, featuresMatchPredicate));

                // Add condition to main query: ensure subquery count matches features size
                predicates.add(cb.equal(subquery, (long) features.size()));
            }

            // Subquery for ensuring the product has all properties
            if (properties != null && !properties.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Product> subRoot = subquery.from(Product.class);
                Join<Product, PropertyValue> subPropertyValueJoin = subRoot.join("propertyValues", JoinType.LEFT);
                Join<PropertyValue, Property> subPropertyJoin = subPropertyValueJoin.join("property", JoinType.LEFT);

                Predicate subqueryPredicate = cb.equal(root.get("id"), subRoot.get("id"));
                Predicate propertiesMatchPredicate = subPropertyJoin.get("propertyId").in(properties);

                subquery.select(cb.countDistinct(subPropertyJoin.get("id")))
                        .where(cb.and(subqueryPredicate, propertiesMatchPredicate));

                predicates.add(cb.equal(subquery, (long) properties.size()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }
    }
