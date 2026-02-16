package com.foodapp.FoodApp.cart.service;

import com.foodapp.FoodApp.auth_users.entity.User;
import com.foodapp.FoodApp.auth_users.service.IUserService;
import com.foodapp.FoodApp.cart.dtos.CartDTO;
import com.foodapp.FoodApp.cart.dtos.CartItemDTO;
import com.foodapp.FoodApp.cart.entity.Cart;
import com.foodapp.FoodApp.cart.entity.CartItem;
import com.foodapp.FoodApp.cart.repository.CartItemRepository;
import com.foodapp.FoodApp.cart.repository.CartRepository;
import com.foodapp.FoodApp.exceptions.NotFoundException;
import com.foodapp.FoodApp.menu.entity.Menu;
import com.foodapp.FoodApp.menu.repository.MenuRepository;
import com.foodapp.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response<?> addItemToCart(CartDTO cartDTO) {
        log.info("INSIDE addItemToCart()");

        Long menuId = cartDTO.getMenuId();
        int quantity = cartDTO.getQuantity();

        User user = userService.getCurrentLoggedInUser();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new NotFoundException("Menu item not found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(()-> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        //Check if the item is already in the  cart
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenu().getId().equals(menuId))
                .findFirst();

        //if present, increment item
        if(optionalCartItem.isPresent()){

            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        if(optionalCartItem.isEmpty()){
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .pricePerUnit(menu.getPrice())
                    .subtotal(menu.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();

            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        //cartRepository.save(cart); DO NOT need to do this because cartItem in cart has cascade set

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item added to cart successfully")
                .build();
    }

    @Override
    public Response<?> incrementItem(Long menuId) {
        log.info("INSIDE incrementItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(()-> new NotFoundException("Cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(()-> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() + 1;

        cartItem.setQuantity(newQuantity);

        cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));

        cartItemRepository.save(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity incremented successfully")
                .build();
    }

    @Override
    public Response<?> decrementItem(Long menuId) {
        log.info("INSIDE decrementItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(()-> new NotFoundException("Cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(()-> new NotFoundException("Menu not found in cart"));

        int newQuantity = 0;

        if(cartItem.getQuantity() <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        newQuantity = cartItem.getQuantity() - 1;

        cartItem.setQuantity(newQuantity);
        cartItem.setSubtotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));
        cartItemRepository.save(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity decremented successfully")
                .build();
    }

    @Override
    public Response<?> removeItem(Long cartItemId) {
        log.info("INSIDE removeItem()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                        .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if(!cart.getCartItems().contains(cartItem)){
            throw new NotFoundException("Cart item does not belong to this user's cart");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cart item removed successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response<CartDTO> getShoppingCart() {
        log.info("INSIDE getShoppingCart()");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        List<CartItem> cartItems = cart.getCartItems();

        List<CartItemDTO> cartItemsDTO = cartItems.stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class))
                .toList();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        //calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(cartItems != null){
            for(CartItem item : cartItems){
                totalAmount = totalAmount.add(item.getSubtotal());
            }
        }

        cartDTO.setTotalAmount(totalAmount); //set the totalAmount

        //remove the review from the response
        if(cartDTO.getCartItems() != null){
            cartDTO.getCartItems()
                    .forEach(item -> item.getMenu().setReviews(null));
        }

        //calculate number of items in total
        int numberOfItemsInTotal = 0;
        for(CartItemDTO item : cartItemsDTO){
            numberOfItemsInTotal += item.getQuantity();
        }

        cartDTO.setQuantity(numberOfItemsInTotal);

        return Response.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping Cart retrieve successfully")
                .data(cartDTO)
                .build();
    }

    @Override
    public Response<?> clearShoppingCart() {
        log.info("INSIDE clearShoppingCart");

        User user = userService.getCurrentLoggedInUser();

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        //Delete cart items from the database first
        cartItemRepository.deleteAll(cart.getCartItems());

        //Clear the cart's items collection
        cart.getCartItems().clear();

        //Update the database
        cartRepository.save(cart);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart cleared successfully")
                .build();
    }
}
