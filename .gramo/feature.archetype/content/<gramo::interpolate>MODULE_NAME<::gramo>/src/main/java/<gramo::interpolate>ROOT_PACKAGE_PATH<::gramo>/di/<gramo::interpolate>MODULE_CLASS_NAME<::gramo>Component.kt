package <gramo::interpolate>ROOT_PACKAGE<::gramo>.di

import com.seatgeek.anvil.feature.FeatureScope
import com.squareup.anvil.annotations.MergeSubcomponent

@FeatureScope
@MergeSubcomponent(FeatureScope::class)
interface <gramo::interpolate>MODULE_CLASS_NAME<::gramo>Component {
    val <gramo::interpolate>MODULE_NAME<::gramo>Fragment: <gramo::interpolate>MODULE_CLASS_NAME<::gramo>Fragment
}