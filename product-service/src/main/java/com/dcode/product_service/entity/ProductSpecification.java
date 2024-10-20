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

            if (features != null && !features.isEmpty()) {
                Join<Product, FeatureValue> featureValueJoin = root.join("featureValues", JoinType.LEFT);
                Join<FeatureValue, Feature> featureJoin = featureValueJoin.join("feature", JoinType.LEFT);

                // Ensure that the product contains all featureIds
                for (String featureId : features) {
                    predicates.add(cb.equal(featureJoin.get("featureId"), featureId));
                }
            }
            if (properties != null && !properties.isEmpty()) {
                Join<Product, PropertyValue> propertyValueJoin = root.join("propertyValues", JoinType.LEFT);
                Join<PropertyValue, Property> propertyJoin = propertyValueJoin.join("property", JoinType.LEFT);

                // Ensure that the product contains all propertyIds
                for (String propertyId : properties) {
                    predicates.add(cb.equal(propertyJoin.get("propertyId"), propertyId));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }
    }
