package com.pleiades.entity.store;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Table(name = "resale_wishlists")
public class ResaleWishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ResaleListing resaleListing;

    public static ResaleWishlist of(User user, ResaleListing resaleListing) {
        ResaleWishlist resaleWishlist = new ResaleWishlist();
        resaleWishlist.setUser(user);
        resaleWishlist.setResaleListing(resaleListing);

        return resaleWishlist;
    }
}
